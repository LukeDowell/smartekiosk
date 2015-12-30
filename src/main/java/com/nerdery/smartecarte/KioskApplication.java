package com.nerdery.smartecarte;


import com.nerdery.smartecarte.handler.TaskManager;
import com.nerdery.smartecarte.network.Multiplexer;
import com.nerdery.smartecarte.service.PollingService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Luke on 12/28/2015.
 */
public class KioskApplication extends Application {

    /** The second scene in the cart checkout process */
    public static Scene cartSelectionScene;

    /** Thread pool */
    public ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Initialize UI
        FXMLLoader loader = new FXMLLoader();

        Parent welcomeRoot = loader.load(getClass().getResource("/fxml/welcome.fxml"));
        Parent cartSelectionRoot = loader.load(getClass().getResource("/fxml/cartselection.fxml"));

        cartSelectionScene = new Scene(cartSelectionRoot, 600, 450);
        cartSelectionScene.getStylesheets().add("/styles/device.css");

        primaryStage.setScene(new Scene(welcomeRoot, 600, 450));
        primaryStage.show();


        // Spin up packet handlers
        executorService.execute(TaskManager::new);

        // Spin up the polling service
        executorService.execute(PollingService::new);

        // Spin up mux
        Multiplexer multiplexer = Multiplexer.getInstance();
        System.out.println(" Kiosk - " + multiplexer);
        executorService.execute(() -> {
            try {
                Thread.sleep(1000);
                multiplexer.multiplex("localhost", 30001);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    public static void main(String[] args) {
        launch(args);
    }
}
