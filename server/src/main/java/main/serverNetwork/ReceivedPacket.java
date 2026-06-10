package main.serverNetwork;

import java.net.SocketAddress;

public class ReceivedPacket {
    private final byte[] data;
    private final SocketAddress clientAddress;

    public ReceivedPacket(byte[] data, SocketAddress clientAddress) {
        this.data = data;
        this.clientAddress = clientAddress;
    }

    public byte[] getData() {
        return data;
    }

    public SocketAddress getClientAddress() {
        return clientAddress;
    }
}
