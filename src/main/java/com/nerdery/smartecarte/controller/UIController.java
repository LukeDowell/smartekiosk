package com.nerdery.smartecarte.controller;

import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.network.Multiplexer;
import com.nerdery.smartecarte.network.Transmit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Transmit transmit = (Transmit) arg;

        outputArea.appendText(transmit.getData() + "\n");
    }

    @FXML
    public void checkoutBtnPressed(ActionEvent event) {
        logger.debug("checkoutBtnPressed");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Multiplexer.getInstance().addObserver(this);
        logger.debug("UIController initialized");
    }
}
