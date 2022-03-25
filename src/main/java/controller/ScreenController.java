package controller;

import com.example.hypera.MainApplication;
import domain.User;
import domain.validator.FriendshipValidator;
import domain.validator.MessageValidator;
import domain.validator.UserValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import repository.database.*;
import service.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ScreenController {


    protected static User currentUser;

    public static Service service;

    public static Stage stage;

    public static void init() throws IOException {

        UserDbRepo repoUser = new UserDbRepo(MainApplication.URL, MainApplication.user, MainApplication.password, new UserValidator());
        FriendshipDbRepo repoFriendship = new FriendshipDbRepo(MainApplication.URL, MainApplication.user, MainApplication.password, new FriendshipValidator());
        MessageDbRepo repoMessage = new MessageDbRepo(MainApplication.URL, MainApplication.user, MainApplication.password, new MessageValidator());

        EventDbRepo repoEvent = new EventDbRepo(MainApplication.URL, MainApplication.user, MainApplication.password);

        service = new Service(repoUser, repoFriendship, repoMessage, repoEvent);
    }

    @FXML
    protected void changeScene(String fxmlString) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxmlString));

        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
    }

    @FXML
    protected void loginScreenSwitch(boolean userAdded) throws IOException{
        stage.hide();
        changeScene("login.fxml");
        stage.show();
        if (userAdded)
            giveConfirmation("User added successfully!");
    }

    @FXML
    protected void registerScreenSwitch() throws IOException{
        stage.hide();
        changeScene("signUp.fxml");
        stage.show();
    }

    @FXML
    protected void adminScreenSwitch() throws IOException {
        changeScene("admin.fxml");
    }

    @FXML
    protected void homeScreenSwitch() throws IOException {
        changeScene("home.fxml");
    }

    @FXML
    protected void friendsScreenSwitch() throws IOException {
        changeScene("friends.fxml");
    }

    @FXML
    protected void messagesScreenSwitch() throws IOException {
        changeScene("messages.fxml");
    }

    @FXML
    protected void eventsScreenSwitch() throws IOException {
        changeScene("events.fxml");
    }

    @FXML
    protected void statisticsScreenSwitch() throws IOException {
        changeScene("statistics.fxml");
    }

    @FXML
    protected void closeWindow() throws IOException {
        System.exit(0);
    }

    @FXML
    protected void giveConfirmation(String s) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText(null);
        a.setContentText(s);
        a.show();
    }

    @FXML
    protected void giveWarning(String s) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(s);
        a.show();
    }
}
