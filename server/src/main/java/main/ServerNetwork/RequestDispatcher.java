package main.ServerNetwork;

import Network.Request;
import Network.Response;
import main.service.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.nio.channels.DatagramChannel;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class RequestDispatcher implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);

    private final ForkJoinPool requestReadPool;
    private final RequestReader requestReader;
    private final RequestHandler requestHandler;
    private final ResponseSender responseSender;

    public RequestDispatcher(
            RequestReader requestReader,
            RequestHandler requestHandler,
            ResponseSender responseSender
    ) {
        this.requestReadPool = new ForkJoinPool();
        this.requestReader = requestReader;
        this.requestHandler = requestHandler;
        this.responseSender = responseSender;
    }

    public void dispatch(DatagramChannel channel, ReceivedPacket receivedPacket) {
        requestReadPool.execute(() -> readRequest(channel, receivedPacket));
    }

    private void readRequest(DatagramChannel channel, ReceivedPacket receivedPacket) {
        try {
            logger.info("Reading packet from: {}", receivedPacket.getClientAddress());
            Request request = requestReader.read(receivedPacket.getData());
            logger.info("Request read: {}", request.getRequestId());

            Thread processingThread = new Thread(
                    () -> processRequest(channel, receivedPacket, request),
                    "request-handler-" + shortRequestId(request)
            );
            processingThread.start();
        } catch (Exception e) {
            logger.error("Error while reading request", e);
        }
    }

    private void processRequest(DatagramChannel channel, ReceivedPacket receivedPacket, Request request) {
        MDC.put("requestId", request.getRequestId());
        try {
            logger.info("Processing request");
            Response response = requestHandler.handle(request);
            logger.info("Response prepared");

            Thread responseThread = new Thread(
                    () -> sendResponse(channel, receivedPacket, request, response),
                    "response-sender-" + shortRequestId(request)
            );
            responseThread.start();
        } catch (Exception e) {
            logger.error("Error while processing request", e);
        } finally {
            MDC.clear();
        }
    }

    private void sendResponse(
            DatagramChannel channel,
            ReceivedPacket receivedPacket,
            Request request,
            Response response
    ) {
        MDC.put("requestId", request.getRequestId());
        try {
            logger.info("Sending response");
            responseSender.send(channel, receivedPacket, response);
            logger.info("Response sent");
        } catch (Exception e) {
            logger.error("Error while sending response", e);
        } finally {
            MDC.clear();
        }
    }

    private String shortRequestId(Request request) {
        String requestId = request.getRequestId();
        if (requestId == null || requestId.length() <= 8) {
            return String.valueOf(requestId);
        }
        return requestId.substring(0, 8);
    }

    @Override
    public void close() {
        requestReadPool.shutdown();
        try {
            if (!requestReadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                requestReadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            requestReadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
