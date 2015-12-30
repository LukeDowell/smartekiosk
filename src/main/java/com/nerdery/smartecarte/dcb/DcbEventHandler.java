package com.nerdery.smartecarte.dcb;

import java.net.SocketAddress;

public interface DcbEventHandler {
	public void handle(SocketAddress source, DcbPacket packet);
}
