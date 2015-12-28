package com.nerdery.smartecarte;

import com.nerdery.smartecarte.dcb.DcbCommand;
import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.network.Multiplexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by ldowell on 12/28/15.
 */
public class Poller implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Poller.class);

    private Multiplexer multiplexer;

    public Poller(Multiplexer multiplexer) {
        this.multiplexer = multiplexer;
        logger.debug("Poller - Initializing polling");
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                multiplexer.transmit(new InetSocketAddress("localhost", 30000), new DcbCommandPacket(DcbCommand.COMMAND_GET_DCB_INFO).getBuffer());
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
