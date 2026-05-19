package main.ServerNetwork;

import Network.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ResponseSender {
    public void send(DatagramChannel channel, ReceivedPacket requestPacket, Response response) throws IOException {
        byte[] responseBytes = serializeResponse(response);
        synchronized (channel) {
            channel.send(ByteBuffer.wrap(responseBytes), requestPacket.getClientAddress());
        }
    }

    private byte[] serializeResponse(Response response) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(response);
            return bos.toByteArray();
        }
    }
}
