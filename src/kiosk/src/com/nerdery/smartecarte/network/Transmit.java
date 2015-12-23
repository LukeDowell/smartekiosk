package com.nerdery.smartecarte.network;

import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by ldowell on 12/23/15.
 */
public class Transmit {

    private final SocketAddress destination;
    private final byte[] data;

    public Transmit(SocketAddress destination, byte[] data) {
        this.destination = destination;
        this.data = data;
    }

    public SocketAddress getDestination() {
        return destination;
    }

    public byte[] getData() {
        return data;
    }
}
