
        package com.smartecarte.mock.dcb;

        import java.io.IOException;
        import java.net.InetAddress;
        import java.net.NetworkInterface;
        import java.net.SocketException;
        import java.net.UnknownHostException;

        import com.nerdery.smartecarte.dcb.Dcb;
        import com.nerdery.smartecarte.dcb.DcbCommandPacket;
        import com.nerdery.smartecarte.dcb.DcbDevice;
        import com.nerdery.smartecarte.network.Multiplexer;
        import com.nerdery.smartecarte.network.Transmit;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

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

        for(int i = 1; i <= 20; i++) {
            DcbDevice device = new DcbDevice(i);
            if(Math.random() > .50) {
                device.setState(DcbDevice.State.OFF);
            } else {
                device.setState(DcbDevice.State.ON);
            }
            addDevice(device);
        }
    }

    public void start() throws IOException {
        logger.info("starting mock dcb");
        logger.debug("dcb: {}", this);

        Multiplexer multiplexor = Multiplexer.getInstance();

        multiplexor.addObserver((server, transmit) -> {
            Transmit transmission = (Transmit) transmit;

            DcbCommandPacket packet = DcbCommandPacket.parse(transmission.getData());
            logger.debug("handler - packet: {}", packet);
            try {
                new DcbCommandTask(multiplexor, this, transmission.getDestination(), packet).call();
            } catch (Exception exception) {
                logger.error("handler - error handling packet: {}  error: {}", packet.getCommand(), exception.getLocalizedMessage());
            }
        });

        multiplexor.multiplex(address.getHostAddress(), port);
    }

    public static void main(String[] args) throws IOException {
        String hostname = "localhost";
        int port = 30000;

        new MockDcb(hostname, port).start();
    }
}