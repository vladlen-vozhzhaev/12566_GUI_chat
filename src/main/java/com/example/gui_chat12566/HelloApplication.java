package com.example.gui_chat12566;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 400); // W, H
        stage.setTitle("Сетевой чат");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        stage.show();
        HelloController helloController = fxmlLoader.getController();
        helloController.connect();
    }

    public static void main(String[] args) {
        launch();
    }
}