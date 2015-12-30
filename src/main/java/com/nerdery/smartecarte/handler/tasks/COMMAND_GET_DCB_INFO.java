package com.nerdery.smartecarte.handler.tasks;

import com.nerdery.smartecarte.dcb.Dcb;
import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.handler.DcbHandler;
import com.nerdery.smartecarte.handler.DcbHandlerTask;
import com.nerdery.smartecarte.model.DcbRepositoryImpl;

import java.util.Arrays;

/**
 * Created by Luke on 12/29/2015.
 */
public class COMMAND_GET_DCB_INFO implements DcbHandler {

    @Override
    public void handle(byte[] buffer) {
        Dcb dcb = new Dcb();

        int manufacturerLength = 6;
        int productionLength = 6;
        int batchLength = 2;
        int partNumberLength = 5;
        int hardwareVersionLength = 3;
        int firmwareVersionLength = 3;

        int offset = 1;

        String manufacturer = new String(buffer, offset, manufacturerLength);
        offset += manufacturerLength;

        String productionDate = new String(buffer, offset, productionLength);
        offset += productionLength;

        String batchNumber = new String(buffer, offset, batchLength);
        offset += batchLength;

        String partNumber = new String(buffer, offset, partNumberLength);
        offset += partNumberLength;

        String hardwareVersion = new String(buffer, offset, hardwareVersionLength);
        offset += hardwareVersionLength;

        String firmwareVersion = new String(buffer, offset, firmwareVersionLength);

        dcb.setManufacturer(manufacturer);
        dcb.setProductionDate(productionDate);
        dcb.setBatchNumber(batchNumber);
        dcb.setPartNumber(partNumber);
        dcb.setHardwareVersion(hardwareVersion);
        dcb.setFirmwareVersion(firmwareVersion);

        dcb.setColumnNumber(1); // TODO:

        DcbRepositoryImpl.getInstance().updateDcb(dcb);
    }
}
