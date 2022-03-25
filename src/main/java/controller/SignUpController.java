package controller;

import domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignUpController extends ScreenController {

    @FXML
    TextField userFirstNameField;
    @FXML
    TextField userLastNameField;
    @FXML
    TextField passwordField;

    @FXML void backClick() throws IOException {
        loginScreenSwitch(false);
    }

    @FXML void signUpClick() throws IOException {

        try {
            User tryUser = new User(userFirstNameField.getText(), userLastNameField.getText(), passwordField.getText());

            if (service.addUser(tryUser) == null){

                loginScreenSwitch(true);
            }
            else
                giveWarning("Invalid name/password.");

        }
        catch (Exception e){
            giveWarning(e.getMessage());
        }
    }

}
