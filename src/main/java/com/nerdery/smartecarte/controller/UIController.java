package com.nerdery.smartecarte.controller;

import com.nerdery.smartecarte.KioskApplication;
import com.nerdery.smartecarte.dcb.DcbCommand;
import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.model.Context;
import com.nerdery.smartecarte.network.Multiplexer;
import com.nerdery.smartecarte.network.Transmit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * Created by ldowell on 12/28/15.
 */
public class UIController implements Observer, Initializable{

    private static final Logger logger = LoggerFactory.getLogger(UIController.class);

    @FXML
    TextArea outputArea;

    @FXML
    Button checkoutBtn;

    @Override
    public void update(Observable o, Object arg) {
        Multiplexer multiplexer = (Multiplexer) o;
        Transmit transmit = (Transmit) arg;

        logger.debug("update - received event from: {} with content: {}", o, arg);
        outputArea.appendText(arg.toString() + "\n");
    }

    @FXML
    public void checkoutBtnPressed(ActionEvent event) {
        logger.debug("checkoutBtnPresed");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Context.getInstance().getMultiplexer().addObserver(this);
        logger.debug("UIController initialized");
    }
}
