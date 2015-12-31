package com.smartecarte.mock.dcb;

import java.net.SocketAddress;
        import java.nio.ByteBuffer;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.concurrent.Callable;

        import com.nerdery.smartecarte.dcb.DcbCommand;
        import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.dcb.DcbDevice;
import com.nerdery.smartecarte.dcb.DcbResponsePacket;
import com.nerdery.smartecarte.network.Transmitter;
import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;


public class DcbCommandTask implements Callable<Void> {
    protected final static Logger logger = LoggerFactory.getLogger(DcbCommandTask.class);

    @FunctionalInterface
    public interface DcbCommandHandler {
        DcbResponsePacket handler(MockDcb dcb, DcbCommandPacket packet);
    }

    static private final DcbCommandHandler COMMAND_GET_ALL_DEVICE_STATE = (MockDcb dcb, DcbCommandPacket packet) -> {
        byte[] buffer = packet.getBuffer();
        byte commandByte = buffer[1];
        byte columnNumber = buffer[2];

        int numDevices = dcb.getDevices().size(); // 20

        ByteBuffer responseBuffer = ByteBuffer.allocate(2 + numDevices); // command byte, column number, 20 devices
        responseBuffer.put(commandByte);
        responseBuffer.put(columnNumber);
        for(DcbDevice device : dcb.getDevices()) {
            if(device.getState().equals(DcbDevice.State.OFF)) {
                responseBuffer.put((byte) 0x00);
            } else {
                responseBuffer.put((byte) 0x01);
            }
        }

        DcbResponsePacket response = new DcbResponsePacket(DcbCommand.COMMAND_GET_ALL_DEVICE_STATE, responseBuffer.array());
        logger.debug("COMMAND_GET_ALL_DEVICE_STATE - return response: {}", response);
        return response;
    };

    static private final DcbCommandHandler COMMAND_SET_DEVICE_STATE = (MockDcb dcb, DcbCommandPacket packet) -> {
        byte[] buffer = packet.getBuffer();
        // 0 and 1 are length and command byte respectively
        int columnNumber = buffer[2];
        int deviceNumber = buffer[3];
        int state = buffer[4];

        if(state == 1) {
            dcb.getDevice(deviceNumber).setState(DcbDevice.State.ON);
        } else {
            dcb.getDevice(deviceNumber).setState(DcbDevice.State.OFF);
        }

        logger.debug("COMMAND_SET_DEVICE_STATE - setting device: {} {} to state: {}", columnNumber, deviceNumber, state);

        byte[] data = {
                (byte) columnNumber,
                (byte) deviceNumber,
                (byte) state
        };
        DcbResponsePacket response = new DcbResponsePacket(DcbCommand.COMMAND_SET_DEVICE_STATE, data);
        return response;
    };

    static private final DcbCommandHandler COMMAND_GET_IP_MAC_ADDRESS = (MockDcb dcb, DcbCommandPacket packet) -> {
        logger.debug("COMMAND_GET_IP_MAC_ADDRESS - dcb: {}  packet: {}", dcb, packet);

        byte[] ip = dcb.getInternetAddress();
        byte[] mac = dcb.getMacAddress();

        byte[] data = new byte[ip.length + mac.length];
        System.arraycopy(ip, 0, data, 0, ip.length);
        System.arraycopy(mac, 0, data, ip.length, mac.length);

        DcbResponsePacket response = new DcbResponsePacket(DcbCommand.COMMAND_GET_IP_MAC_ADDRESS, data);
        logger.debug("COMMAND_GET_IP_MAC_ADDRESS - return response: {}", response);

        return response;
    };

    static private final DcbCommandHandler COMMAND_GET_DCB_PARAMETERS = (MockDcb dcb, DcbCommandPacket packet) -> {
        logger.debug("COMMAND_GET_DCB_PARAMETERS - dcb: {}  packet: {}", dcb, packet);

        byte[] startNumber = new byte[]{0, 10};
        byte[] cabinetType = new byte[]{1};
        byte[] boxQuantity = new byte[]{10};
        byte[] boxConfiguration = new byte[]{'M', 'M', 'S', 'S', 'S', 'S', 'S', 'S', 'M', 'L'};

        int length = startNumber.length + cabinetType.length + boxQuantity.length + boxConfiguration.length;
        ByteBuffer buffer = ByteBuffer.allocate(length);

        buffer.put(startNumber);
        buffer.put(cabinetType);
        buffer.put(boxQuantity);
        buffer.put(boxConfiguration);

        DcbResponsePacket response = new DcbResponsePacket(DcbCommand.COMMAND_GET_DCB_PARAMETERS, buffer.array());
        logger.debug("COMMAND_GET_DCB_PARAMETERS - return response: {}", response);

        return response;
    };

    static private final DcbCommandHandler COMMAND_GET_DCB_COLUMN_NUMBER = (MockDcb dcb, DcbCommandPacket packet) -> {
        logger.debug("COMMAND_GET_DCB_COLUMN_NUMBER - dcb: {}  packet: {}", dcb, packet);

        byte[] data = new byte[1];
        data[0] = (byte) dcb.getColumnNumber().intValue();

        DcbResponsePacket response = new DcbResponsePacket(DcbCommand.COMMAND_GET_DCB_COLUMN_NUMBER, data);
        logger.debug("COMMAND_GET_DCB_COLUMN_NUMBER - return response: {}", response);

        return response;
    };

    static private final DcbCommandHandler COMMAND_GET_DCB_INFO = (MockDcb dcb, DcbCommandPacket packet) -> {
        logger.debug("COMMAND_GET_DCB_INFO - dcb: {}  packet: {}", dcb, packet);

        byte[] manufacturer = dcb.getManufacturer().getBytes();
        byte[] productionDate = dcb.getProductionDate().getBytes();
        byte[] batchNumber = dcb.getBatchNumber().getBytes();
        byte[] partNumber = dcb.getPartNumber().getBytes();
        byte[] hardwareVersion = dcb.getHardwareVersion().getBytes();
        byte[] firmwareVersion = dcb.getFirmwareVersion().getBytes();

        int length = manufacturer.length + productionDate.length + batchNumber.length + partNumber.length + hardwareVersion.length + firmwareVersion.length;
        ByteBuffer buffer = ByteBuffer.allocate(length);

        buffer.put(manufacturer);
        buffer.put(productionDate);
        buffer.put(batchNumber);
        buffer.put(partNumber);
        buffer.put(hardwareVersion);
        buffer.put(firmwareVersion);

        DcbResponsePacket response = new DcbResponsePacket(DcbCommand.COMMAND_GET_DCB_INFO, buffer.array());
        logger.debug("COMMAND_GET_DCB_INFO - return response: {}", response);

        return response;
    };

    static private final Map<DcbCommand, DcbCommandHandler> map = new HashMap<DcbCommand, DcbCommandHandler>();

    static {
        map.put(DcbCommand.COMMAND_SET_DEVICE_STATE, COMMAND_SET_DEVICE_STATE);
        map.put(DcbCommand.COMMAND_GET_ALL_DEVICE_STATE, COMMAND_GET_ALL_DEVICE_STATE);
        map.put(DcbCommand.COMMAND_GET_IP_MAC_ADDRESS, COMMAND_GET_IP_MAC_ADDRESS);
        map.put(DcbCommand.COMMAND_GET_DCB_PARAMETERS, COMMAND_GET_DCB_PARAMETERS);
        map.put(DcbCommand.COMMAND_GET_DCB_COLUMN_NUMBER, COMMAND_GET_DCB_COLUMN_NUMBER);
        map.put(DcbCommand.COMMAND_GET_DCB_INFO, COMMAND_GET_DCB_INFO);
    }

    private Transmitter transmitter;
    private MockDcb dcb;
    private SocketAddress source;
    private DcbCommandPacket packet;

    public DcbCommandTask(Transmitter transmitter, MockDcb dcb, SocketAddress source, DcbCommandPacket packet) {
        this.transmitter = transmitter;
        this.dcb = dcb;
        this.source = source;
        this.packet = packet;
    }

    @Override
    public Void call() throws Exception {
        logger.debug("call");

        logger.debug("call - command: {}", packet.getCommand());

        DcbCommandHandler handler = map.get(packet.getCommand());
        logger.debug("call - handler: {}", handler);

        if (handler != null) {
            transmitter.transmit(source, handler.handler(dcb, packet).getBuffer());
        } else {
            logger.warn("call - cannot find command handler for: {}", packet.getCommand());
        }

        return null;
    }
}