package main.serverNetwork;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UdpRequestReceiver {
    public ReceivedPacket receive(DatagramChannel channel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        SocketAddress clientAddress = channel.receive(buffer);

        if (clientAddress == null) {
            return null;
        }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        return new ReceivedPacket(data, clientAddress);
    }
}
