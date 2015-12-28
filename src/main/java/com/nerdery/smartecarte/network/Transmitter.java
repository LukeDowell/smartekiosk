package com.nerdery.smartecarte.network;

import java.net.SocketAddress;

/**
 * Created by ldowell on 12/28/15.
 */
public interface Transmitter {

    void transmit(SocketAddress dest, byte[] data);
}
