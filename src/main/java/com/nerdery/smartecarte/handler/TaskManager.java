package com.nerdery.smartecarte.handler;

import com.nerdery.smartecarte.dcb.DcbCommand;
import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.network.Multiplexer;
import com.nerdery.smartecarte.network.Transmit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Luke on 12/29/2015.
 */
public class TaskManager implements Observer {

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    public TaskManager() {
        Multiplexer mux = Multiplexer.getInstance();
        mux.addObserver(this);
        System.out.println(mux);
        logger.debug("TaskManager started.");
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            Transmit transmit = (Transmit) arg;
            byte[] data = transmit.getData();
            DcbCommand command = DcbCommand.valueOf(data[0]);

            logger.debug("taskmanager - update - packet received: {}", command);
            new DcbHandlerTask(command, data).call();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
