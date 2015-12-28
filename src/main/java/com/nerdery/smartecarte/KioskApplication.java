package com.nerdery.smartecarte;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Luke on 12/28/2015.
 */
public class KioskApplication extends Application {

    public static Scene cartSelectionScene;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();

        Parent welcomeRoot = loader.load(getClass().getResource("/fxml/welcome.fxml"));
        Parent cartSelectionRoot = loader.load(getClass().getResource("/fxml/cartselection.fxml"));

        KioskApplication.cartSelectionScene = new Scene(cartSelectionRoot, 600, 450);
        primaryStage.setScene(new Scene(welcomeRoot, 600, 450));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
