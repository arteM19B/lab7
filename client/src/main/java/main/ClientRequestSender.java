package main;

import Network.CommandArgument;
import Network.CommandType;
import Network.Request;
import Network.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ClientRequestSender {
    private static final Logger logger = LoggerFactory.getLogger(ClientRequestSender.class);

    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;

    public ClientRequestSender(DatagramSocket socket, InetAddress address, int port) {
        this.socket = socket;
        this.address = address;
        this.port = port;
    }

    public String send(CommandType commandType, CommandArgument argument, String login, String password) throws IOException, ClassNotFoundException {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            logger.info("Preparing command: {}", commandType);
            Request request = new Request(requestId, commandType, argument, login, password);
            byte[] data = serialize(request);

            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            logger.info("Sending request to {}:{}", address.getHostName(), port);
            socket.send(packet);

            logger.info("Waiting for server response");
            byte[] buffer = new byte[65536];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);

            byte[] responseData = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
            Response response = deserializeResponse(responseData);
            logger.info("Response received");

            return response.getMessage();
        } finally {
            MDC.clear();
        }
    }

    private Response deserializeResponse(byte[] data) throws IOException, ClassNotFoundException {
        logger.debug("Response deserialization started");
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Response) ois.readObject();
        }
    }

    private byte[] serialize(Request request) throws IOException {
        logger.debug("Request serialization started");
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(request);
            return bos.toByteArray();
        }
    }
}
