package com.nerdery.smartecarte.model;

import com.nerdery.smartecarte.dcb.Dcb;
import com.nerdery.smartecarte.dcb.DcbDevice;
import com.nerdery.smartecarte.dcb.DcbEvent;
import com.nerdery.smartecarte.model.event.DcbChangedEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ldowell on 12/28/15.
 */
public class DcbRepositoryImpl extends DcbRepository {

    private Map<Integer, Dcb> dcbs;

    public DcbRepositoryImpl() {
        dcbs = new HashMap<>();
    }

    @Override
    public void initialize() {
        dcbs = new HashMap<>();
    }

    @Override
    public void addDcb(Dcb dcb) {
        dcbs.put(dcb.getColumnNumber(), dcb);
        setChanged();
        notifyObservers(new DcbChangedEvent(DcbEvent.DCB_ADDED, dcb.getColumnNumber(), 0));
    }

    @Override
    public void addDevice(int columnNumber, DcbDevice device) {
        dcbs.get(columnNumber).addDevioe(device);
        setChanged();
        notifyObservers(new DcbChangedEvent(DcbEvent.DEVICE_ADDED, columnNumber, device.getId()));
    }

    @Override
    public Collection<Dcb> getAllDcbs() {
        return dcbs.values();
    }

    @Override
    public Collection<DcbDevice> getAllDevices() {
        Collection<DcbDevice> devices = new ArrayList<>();
        for(Dcb dcb : dcbs.values()) {
            devices.addAll(dcb.getDevices().stream().collect(Collectors.toList()));
        }
        return devices;
    }

    @Override
    public Dcb getDcb(int columnNumber) {
        return dcbs.get(columnNumber);
    }

    @Override
    public DcbDevice getDevice(int columnNumber, int id) {
        Dcb dcb = getDcb(columnNumber);
        if(dcb != null) {
            return dcb.getDevice(id);
        }
        return null;
    }

    @Override
    DcbDevice updateDevice(int columnNumber, int id, DcbDevice.State state) {
        Dcb dcb = getDcb(columnNumber);
        if(dcb != null) {
            DcbDevice.State previousState = dcb.getDevice(id).getState();
            dcb.getDevice(id).setState(state);
            if(!previousState.equals(state)) {
                setChanged();
                notifyObservers(new DcbChangedEvent(DcbEvent.DEVICE_STATE_CHANGED, columnNumber, id));
            }
            return dcb.getDevice(id);
        }

        return null;
    }

    private DcbRepository instance;

    public DcbRepository getInstance() {
        synchronized (this) {
            if(instance == null) {
                instance = new DcbRepositoryImpl();
            }
            return instance;
        }
    }
}
