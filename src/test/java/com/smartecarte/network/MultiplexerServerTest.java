package com.smartecarte.network;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartecarte.util.HexDump;

public class MultiplexerServerTest  {
	protected final static Logger logger = LoggerFactory.getLogger(MultiplexerServerTest.class);

	public static void main(String[] args) throws IOException {
		String hostname = "localhost";
		int port = 30000;
		
		Multiplexer multiplexer = new Multiplexer();
		
		multiplexer.addEventHandler((source, buffer) -> {
			logger.info("handler - source: {}  buffer: {}", source, buffer);
			HexDump.dump(logger, buffer);
			
			multiplexer.transmit(source, buffer);
		});
		
		multiplexer.start(hostname, port);
	}
}
