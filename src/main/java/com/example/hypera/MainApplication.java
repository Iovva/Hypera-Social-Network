package com.example.hypera;

import controller.ScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;



public class MainApplication extends Application {

    // !! CHANGE THESE TO YOUR POSTGRESQL DATABASE !! //
    public static String URL = "jdbc:postgresql://localhost:5432/labmap";
    public static String user = "postgres";
    public static String password = "postgres";
    // !! CHANGE THESE TO YOUR POSTGRESQL DATABASE !! //
    
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        ScreenController.init();
        ScreenController.stage = stage;

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}