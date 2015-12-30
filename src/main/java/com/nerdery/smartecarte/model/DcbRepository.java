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

    public abstract void initialize();

    public abstract void addDcb(Dcb dcb);

    public abstract void addDevice(int columnNumber, DcbDevice device);

    public abstract void updateDcb(Dcb dcb);

    public abstract Collection<Dcb> getAllDcbs();

    public abstract Collection<DcbDevice> getAllDevices();

    public abstract Dcb getDcb(int columnNumber);

    public abstract DcbDevice getDevice(int columnNumber, int id);

    public abstract void updateDevice(int columnNumber, int id, DcbDevice.State state);

    public abstract void updateDevice(int columnNumber, DcbDevice device);
}
