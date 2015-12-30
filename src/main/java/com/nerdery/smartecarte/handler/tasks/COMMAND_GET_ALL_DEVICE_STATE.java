package com.nerdery.smartecarte.handler.tasks;

import com.nerdery.smartecarte.dcb.DcbDevice;
import com.nerdery.smartecarte.handler.DcbHandler;
import com.nerdery.smartecarte.model.DcbRepository;
import com.nerdery.smartecarte.model.DcbRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ldowell on 12/30/15.
 */
public class COMMAND_GET_ALL_DEVICE_STATE implements DcbHandler {

    private static final Logger logger = LoggerFactory.getLogger(COMMAND_GET_ALL_DEVICE_STATE.class);
    @Override
    public void handle(byte[] buffer) {

        logger.debug("GetAllDeviceState Handler - handling");

        int columnNumber = buffer[2];
        int numDevices = 20;
        int offset = 2;

        for(int i = 1; i <= numDevices; i++) {
            DcbDevice dcbDevice = new DcbDevice(i);
            if(buffer[i + offset] == 0x00) {
                dcbDevice.setState(DcbDevice.State.OFF);
            } else {
                dcbDevice.setState(DcbDevice.State.ON);
            }
            DcbRepository repo = DcbRepositoryImpl.getInstance();
            repo.updateDevice(columnNumber, dcbDevice);
        }
    }
}
