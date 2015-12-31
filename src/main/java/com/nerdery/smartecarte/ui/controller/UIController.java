package com.nerdery.smartecarte.ui.controller;

import com.nerdery.smartecarte.dcb.DcbCommand;
import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.dcb.DcbDevice;
import com.nerdery.smartecarte.model.DcbRepositoryImpl;
import com.nerdery.smartecarte.model.event.DcbChangedEvent;
import com.nerdery.smartecarte.network.Multiplexer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 * Created by ldowell on 12/28/15.
 */
public class UIController implements Observer, Initializable {

    private static final Logger logger = LoggerFactory.getLogger(UIController.class);

    @FXML
    TextArea outputArea;

    @FXML
    Button checkoutBtn;

    @FXML
    FlowPane cartFlowPane;

    @FXML
    ProgressIndicator progressIndicator;

    private Node selectedNode = null;

    private int selectedCartId = 0;

    @Override
    public void update(Observable o, Object arg) {
        logger.debug("UIController -  event received");
        DcbChangedEvent event = (DcbChangedEvent) arg;

        outputArea.appendText(event.getEvent() + " - " + event.getColumnNumber() + " : " + event.getDeviceNumber() + "\n");

        switch(event.getEvent()) {
            case DEVICE_ADDED:
            case DEVICE_STATE_CHANGED:
                try {
                    Platform.runLater(() -> {
                        updateCarts();
                        if(selectedCartId == event.getDeviceNumber()) {
                            progressIndicator.setProgress(100d);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    @FXML
    public void checkoutBtnPressed(ActionEvent event) {
        if(selectedNode != null) {
            String sId = ((Label) selectedNode.lookup("#idLabel")).getText();
            int columnNumber = 1;
            int deviceId = Integer.parseInt(sId);
            int state = DcbRepositoryImpl.getInstance().getDevice(columnNumber, deviceId).getState() == DcbDevice.State.OFF ? 1 : 0;
            byte[] data = {
                    0x01,
                    (byte) deviceId,
                    (byte) state
            };
            Multiplexer.getInstance().transmit(Multiplexer.MOCKADDRESS, new DcbCommandPacket(DcbCommand.COMMAND_SET_DEVICE_STATE, data).getBuffer());
            progressIndicator.setVisible(true);
            progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            selectedCartId = deviceId;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DcbRepositoryImpl.getInstance().addObserver(this);
        logger.debug("UIController initialized");
    }

    private void cartClicked(MouseEvent event) {
        if(selectedNode != null) {
            selectedNode.getStyleClass().remove("device-selected");
        }
        Node sourceNode = (Node) event.getSource();
        sourceNode.getStyleClass().add("device-selected");
        String id = ((Label) sourceNode.lookup("#idLabel")).getText();

        selectedNode = sourceNode;
        logger.debug("UIController - Node clicked with id: " + id);
    }

    private void updateCarts() {
        try {
            Collection<DcbDevice> devices = DcbRepositoryImpl.getInstance().getAllDevices();
            Collection<Node> children = new LinkedHashSet<>();

            for(DcbDevice d : devices) {

                Node deviceNode = FXMLLoader.load(getClass().getResource("/fxml/device.fxml"));
                ((Label) deviceNode.lookup("#idLabel")).setText(d.getId().toString());
                if(d.getState().equals(DcbDevice.State.ON)) {
                    deviceNode.getStyleClass().add("device-enabled");
                } else {
                    deviceNode.getStyleClass().add("device-disabled");
                }

                deviceNode.setOnMouseClicked(this::cartClicked);
                children.add(deviceNode);
            }

            cartFlowPane.getChildren().setAll(children);
            selectedNode = null;
            progressIndicator.setProgress(100d);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
