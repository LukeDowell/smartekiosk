package com.nerdery.smartecarte.controller;

import com.nerdery.smartecarte.KioskApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by Luke on 12/28/2015.
 */
public class WelcomeController {

    @FXML
    GridPane gridPane;

    @FXML
    public void btnPressed(ActionEvent event) {
        Stage stage = (Stage) gridPane.getScene().getWindow();
        stage.setScene(KioskApplication.cartSelectionScene);
    }
}
