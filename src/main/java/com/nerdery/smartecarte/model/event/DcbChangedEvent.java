package com.nerdery.smartecarte.model.event;

import com.nerdery.smartecarte.dcb.DcbEvent;

/**
 * Created by Luke on 12/29/2015.
 */
public class DcbChangedEvent {

    private DcbEvent event;
    private int columnNumber;
    private int deviceNumber;

    /**
     * A representation of a change occuring on the DCB.
     * @param event
     *      The event that occurred
     * @param columnNumber
     *      The column number of the device or dcb that
     *      the event refers to
     * @param deviceNumber
     *      The id of the device that the event refers to
     */
    public DcbChangedEvent(DcbEvent event, int columnNumber, int deviceNumber) {
        this.event = event;
        this.columnNumber = columnNumber;
        this.deviceNumber = deviceNumber;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public DcbEvent getEvent() {
        return event;
    }

    public int getColumnNumber() {
        return columnNumber;
    }
}
