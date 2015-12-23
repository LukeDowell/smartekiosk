package com.smartecarte.network;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartecarte.util.HexDump;

public class MultiplexerClientTest  {
	protected final static Logger logger = LoggerFactory.getLogger(MultiplexerClientTest.class);

	public static void main(String[] args) throws IOException {
		String hostname = "localhost";
		int port = 30001;
		
		Multiplexer multiplexer = new Multiplexer();
		
		multiplexer.addEventHandler((source, buffer) -> {
			logger.info("handler - source: {}  buffer: {}", source, buffer);
			HexDump.dump(logger, buffer);
			multiplexer.stop();
		});
		
		InetSocketAddress destination = new InetSocketAddress("localhost", 30000);
		
		multiplexer.transmit(destination, new byte[] { 't', 'e', 's', 't' });
		multiplexer.start(hostname, port);
	}
}
