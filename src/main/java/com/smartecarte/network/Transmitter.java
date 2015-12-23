package com.smartecarte.network;

import java.net.SocketAddress;

public interface Transmitter {
	public void transmit(SocketAddress destination, byte[] data);
}
