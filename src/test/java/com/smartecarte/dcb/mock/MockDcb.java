package com.smartecarte.dcb.mock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartecarte.dcb.Dcb;
import com.smartecarte.dcb.DcbCommandPacket;
import com.smartecarte.network.Multiplexer;
import com.smartecarte.util.HexDump;

public class MockDcb extends Dcb {
	protected final static Logger logger = LoggerFactory.getLogger(MockDcb.class);
	
	private InetAddress address;
	private NetworkInterface network;
	private int port;

	public MockDcb(String name, int port) throws UnknownHostException, SocketException {
		address = InetAddress.getByName(name);
		logger.debug("address: {}", address);
		
		this.port = port;
		
		network = NetworkInterface.getByInetAddress(address);
		logger.debug("network: {}", network);
		
		setInternetAddress(address.getAddress());
		
		byte[] mac = network.getHardwareAddress();
		if (mac == null) {
			mac = new byte[6];
		}
		setMacAddress(mac);
		
		setManufacturer("mocdcb");
		setProductionDate("130815");
		setBatchNumber("01");
		setPartNumber("12345");
		setHardwareVersion("001");
		setFirmwareVersion("001");
		
		setColumnNumber(1);
	}

	public void start() throws IOException {
		logger.info("starting mock dcb");
		logger.debug("dcb: {}", this);
		
		Multiplexer multiplexor = new Multiplexer();
		
		multiplexor.addEventHandler((source, buffer) -> {
			logger.debug("handler - source: {}  buffer: {}", source, buffer);
			HexDump.dump(logger, buffer);
			
			DcbCommandPacket packet = DcbCommandPacket.parse(buffer);
			logger.debug("handler - packet: {}", packet);
			try {
				new DcbCommandTask(multiplexor, this, source, packet).call();
			} catch (Exception exception) {
		        logger.error("handler - error handling packet: {}  error: {}", packet.getCommand(), exception.getLocalizedMessage());
			}
		});

		multiplexor.start(address.getHostAddress(), port);
	}

	public static void main(String[] args) throws IOException {
		String hostname = "localhost";
		int port = 30000;
		
		new MockDcb(hostname, port).start();
	}
}
