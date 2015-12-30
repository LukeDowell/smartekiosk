package com.nerdery.smartecarte.model;

import com.nerdery.smartecarte.dcb.Dcb;
import com.nerdery.smartecarte.dcb.DcbDevice;
import com.nerdery.smartecarte.dcb.DcbEvent;
import com.nerdery.smartecarte.model.event.DcbChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ldowell on 12/28/15.
 */
public class DcbRepositoryImpl extends DcbRepository {

    private static final Logger logger = LoggerFactory.getLogger(DcbRepository.class);

    private Map<Integer, Dcb> dcbs;

    private DcbRepositoryImpl() {
        dcbs = new HashMap<>();
    }

    @Override
    public void initialize() {
        dcbs = new HashMap<>();
    }

    @Override
    public void addDcb(Dcb dcb) {
        dcbs.put(dcb.getColumnNumber(), dcb);
        logger.debug("DcbRepository - dcb added: {}", dcb.getColumnNumber());

        setChanged();
        notifyObservers(new DcbChangedEvent(DcbEvent.DCB_ADDED, dcb.getColumnNumber(), 0));

    }

    @Override
    public void addDevice(int columnNumber, DcbDevice device) {
        dcbs.get(columnNumber).addDevice(device);
        logger.debug("DcbRepository - device added: {} {}", columnNumber, device.getId());

        setChanged();
        notifyObservers(new DcbChangedEvent(DcbEvent.DEVICE_ADDED, columnNumber, device.getId()));
    }

    @Override
    public void updateDcb(Dcb dcb) {
        Dcb currentDcb = getDcb(dcb.getColumnNumber());
        if(currentDcb != null) {

            dcbs.put(dcb.getColumnNumber(), dcb);
            setChanged();
            notifyObservers(new DcbChangedEvent(DcbEvent.DCB_UPDATED, dcb.getColumnNumber(), 0));

        } else {
            addDcb(dcb);
        }
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
    public void updateDevice(int columnNumber, int id, DcbDevice.State state) {
        Dcb dcb = getDcb(columnNumber);
        if(dcb != null) {
            DcbDevice device = new DcbDevice(id);
            device.setState(state);
            updateDevice(columnNumber, device);
        }
    }

    @Override
    public void updateDevice(int columnNumber, DcbDevice device) {
        // Grab the dcb
        Dcb dcb = getDcb(columnNumber);

        // If the dcb is not null
        if(dcb != null) {

            // Grab the existing device with the same id as the parameter device
            DcbDevice currentDevice = dcb.getDevice(device.getId());

            // If it is null, then add it and be done
            if(currentDevice == null) {
                addDevice(columnNumber, device);

            } else {
                // Otherwise, compare states and update
                DcbDevice.State state = device.getState();
                DcbDevice.State previousState = currentDevice.getState();
                currentDevice.setState(state);

                if(!state.equals(previousState)) {
                    setChanged();
                    notifyObservers(new DcbChangedEvent(DcbEvent.DEVICE_STATE_CHANGED, columnNumber, device.getId()));
                }
            }
        }
    }

    private static DcbRepository instance;

    public static DcbRepository getInstance() {
        if(instance == null) {
            instance = new DcbRepositoryImpl();
        }
        return instance;
    }
}
