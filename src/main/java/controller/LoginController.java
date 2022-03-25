package controller;

import domain.User;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import repository.database.FriendshipDbRepo;

import javafx.fxml.FXML;

import java.io.IOException;

public class LoginController extends ScreenController{

    @FXML
    TextField userField;
    @FXML
    TextField passField;

    @FXML
    protected void loginClick() throws IOException {
        try {

            String[] nameSplit = userField.getText().split("\\s+");
            User tryUser = new User(nameSplit[0], nameSplit[1], passField.getText());

            if (ScreenController.service.loginUser(tryUser))
                startHomePage(tryUser);
            else
                giveWarning("Invalid name/password.");
        }
        catch (ArrayIndexOutOfBoundsException ex){
            giveWarning("Please enter your full name!");
        }
    }

    @FXML void signUpClick() throws IOException {
        registerScreenSwitch();
    }


    @FXML
    protected void startHomePage(User tryUser) throws IOException {

        User currentUser = service.findUserByName(tryUser.getFirstName(), tryUser.getLastName());

        ScreenController.currentUser = currentUser;

        if (ScreenController.service.checkIfAdmin(currentUser))
            adminScreenSwitch();
        else
            homeScreenSwitch();
    }

    public void nameKeyPressed(KeyEvent keyEvent) throws IOException {
        if(keyEvent.getCode().toString().equals("ENTER")) {
            loginClick();
        }
    }

    public void passwordKeyPressed(KeyEvent keyEvent) throws IOException {
        if(keyEvent.getCode().toString().equals("ENTER")) {
            loginClick();
        }
    }
}
