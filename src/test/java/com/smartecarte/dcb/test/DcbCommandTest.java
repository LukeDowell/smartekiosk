package com.smartecarte.dcb.test;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartecarte.dcb.DcbCommand;
import com.smartecarte.dcb.DcbCommandPacket;
import com.smartecarte.network.Multiplexer;
import com.smartecarte.util.HexDump;

public class DcbCommandTest {
	protected final static Logger logger = LoggerFactory.getLogger(DcbCommandTest.class);

	public static void main(String[] args) throws IOException {
		String hostname = "localhost";
		int port = 30000;

		Multiplexer multiplexor = new Multiplexer();
		
		multiplexor.addEventHandler((source, buffer) -> {
			logger.debug("handler - source: {}  packet: {}", source, buffer);
			logger.debug("handler - command: {}", DcbCommand.valueOf(buffer[0]));
			HexDump.dump(logger, buffer);
		});
		
		InetSocketAddress broadcast = new InetSocketAddress("255.255.255.255", port);
		InetSocketAddress destination = new InetSocketAddress("localhost", port);
		
		multiplexor.transmit(broadcast, new DcbCommandPacket(DcbCommand.COMMAND_GET_IP_MAC_ADDRESS).getBuffer());
		multiplexor.transmit(destination, new DcbCommandPacket(DcbCommand.COMMAND_GET_DCB_INFO).getBuffer());
		multiplexor.transmit(destination, new DcbCommandPacket(DcbCommand.COMMAND_GET_DCB_COLUMN_NUMBER).getBuffer());
		multiplexor.transmit(destination, new DcbCommandPacket(DcbCommand.COMMAND_GET_DCB_PARAMETERS).getBuffer());
		
		multiplexor.start(hostname, 30001);
	}
}
