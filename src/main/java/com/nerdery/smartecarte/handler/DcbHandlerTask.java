package com.nerdery.smartecarte.handler;

import com.nerdery.smartecarte.dcb.DcbCommand;
import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.handler.tasks.COMMAND_GET_DCB_INFO;
import com.nerdery.smartecarte.handler.tasks.COMMAND_GET_IP_MAC_ADDRESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Luke on 12/28/2015.
 */
public class DcbHandlerTask implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(DcbHandlerTask.class);

    private DcbCommand command;
    private byte[] buffer;

    private static Map<DcbCommand, DcbHandler> dcbHandlerMap = new HashMap<>();

    static {
        dcbHandlerMap.put(DcbCommand.COMMAND_GET_DCB_INFO, new COMMAND_GET_DCB_INFO());
        dcbHandlerMap.put(DcbCommand.COMMAND_GET_IP_MAC_ADDRESS, new COMMAND_GET_IP_MAC_ADDRESS());
        logger.debug("DcbHandlerTasks initialized");
    }

    public DcbHandlerTask(DcbCommand command, byte[] buffer) {
        this.command = command;
        this.buffer = buffer;
    }

    @Override
    public Void call() throws Exception {
        DcbHandler handler = dcbHandlerMap.get(command);
        if(handler != null) {
            handler.handle(buffer);
        } else {
            logger.debug("call - Cannot find handler for command: {}", command);
        }
        return null;
    }
}
