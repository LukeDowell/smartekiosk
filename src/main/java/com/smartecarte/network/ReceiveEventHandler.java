package com.smartecarte.network;

import java.net.SocketAddress;

public interface ReceiveEventHandler {
	public void handle(SocketAddress source, byte[] data);
}
