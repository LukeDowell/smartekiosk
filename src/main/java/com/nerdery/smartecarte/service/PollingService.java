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

    private static final int PERIOD = 10000; // How often we want to poll in millis

    public PollingService() {
        logger.debug("PollingService - started");
        Multiplexer multiplexer = Multiplexer.getInstance();
        multiplexer.transmit(mockAddress, new DcbCommandPacket(DcbCommand.COMMAND_GET_DCB_INFO).getBuffer());
        multiplexer.transmit(mockAddress, new DcbCommandPacket(DcbCommand.COMMAND_GET_IP_MAC_ADDRESS).getBuffer());
        multiplexer.transmit(mockAddress, new DcbCommandPacket(DcbCommand.COMMAND_GET_ALL_DEVICE_STATE, new byte[]{1}).getBuffer());

        long lastHeartbeatTime = 0;

        while(!Thread.currentThread().isInterrupted()) {


            try {

                if(System.currentTimeMillis() - lastHeartbeatTime >= 15000) {
                    logger.debug("PollingService - heartbeat");
                    lastHeartbeatTime = System.currentTimeMillis();
                }

                // The id of the DCB we want to check
                byte[] data = { 1 };
                multiplexer.transmit(mockAddress, new DcbCommandPacket(DcbCommand.COMMAND_GET_ALL_DEVICE_STATE, data).getBuffer());
                Thread.sleep(PERIOD);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
