package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.IOException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class HomeController extends ScreenController {
    @FXML
    private Label label_numeUtilizator;
    @FXML
    private Circle profile_picture;

    private Image image;
    private String default_path = "local/MainScreens/HomeScreen/default.png";
    private String profilePicture_path;


    @FXML
    public void initialize(){
        File currentDirFile = new File("");
        profilePicture_path = currentDirFile.toURI() + "/ProfilePictures/" + currentUser.getId();

        label_numeUtilizator.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        image = service.getImage(currentUser.getId());

        profile_picture.setFill(new ImagePattern(image));
    }

    @FXML
    public void clickedProfilePicture() {
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(imageFilter);
        File file = chooser.showOpenDialog(profile_picture.getScene().getWindow());
        if (file != null) {
            System.out.println(file.getAbsolutePath());

            File currentDirFile = new File("");
            File destination = new File( currentDirFile.getAbsolutePath() + "/ProfilePictures/" + currentUser.getId());

            try {
                try {
                    Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                catch(NoSuchFileException ne){
                    Files.createDirectories(Paths.get( currentDirFile.getAbsolutePath() + "/ProfilePictures/"));
                    Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                image = new Image(profilePicture_path,256,256,false,true);
                //profile_picture.setImage(image);
                profile_picture.setFill(new ImagePattern(image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void logOutClick(ActionEvent actionEvent) throws IOException {
        changeScene("login.fxml");
    }
}
