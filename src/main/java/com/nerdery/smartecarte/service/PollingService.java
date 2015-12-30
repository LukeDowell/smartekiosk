package com.nerdery.smartecarte.service;

import com.nerdery.smartecarte.dcb.DcbCommand;
import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.network.Multiplexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by ldowell on 12/28/15.
 */
public class PollingService {

    private static final Logger logger = LoggerFactory.getLogger(PollingService.class);

    private InetSocketAddress mockAddress = new InetSocketAddress("localhost", 30000);

    public PollingService() {
        Multiplexer multiplexer = Multiplexer.getInstance();
        multiplexer.transmit(mockAddress, new DcbCommandPacket(DcbCommand.COMMAND_GET_DCB_INFO).getBuffer());
        multiplexer.transmit(mockAddress, new DcbCommandPacket(DcbCommand.COMMAND_GET_IP_MAC_ADDRESS).getBuffer());
        multiplexer.transmit(mockAddress, new DcbCommandPacket(DcbCommand.COMMAND_GET_DEVICE_STATE).getBuffer());
    }

}
