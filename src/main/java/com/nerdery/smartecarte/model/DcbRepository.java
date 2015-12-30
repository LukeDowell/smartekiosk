package com.nerdery.smartecarte.model;

import com.nerdery.smartecarte.dcb.Dcb;
import com.nerdery.smartecarte.dcb.DcbDevice;

import java.util.Collection;
import java.util.List;
import java.util.Observable;

/**
 * Created by Luke on 12/28/2015.
 */
public abstract class DcbRepository extends Observable {

    abstract void initialize();

    abstract void addDcb(Dcb dcb);

    abstract void addDevice(int columnNumber, DcbDevice device);

    abstract Collection<Dcb> getAllDcbs();

    abstract Collection<DcbDevice> getAllDevices();

    abstract Dcb getDcb(int columnNumber);

    abstract DcbDevice getDevice(int columnNumber, int id);

    abstract DcbDevice updateDevice(int columnNumber, int id, DcbDevice.State state);
}
