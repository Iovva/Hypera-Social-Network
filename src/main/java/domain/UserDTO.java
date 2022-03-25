package domain;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDTO {

    String firstName;
    String lastName;
    String fullName;

    LocalDateTime date;
    String formatted_date;

    Image profilePicture;
    Circle profileCircle;

    Long idUser;

    public UserDTO(String firstName, String lastName, LocalDateTime date) {
        this.fullName = firstName + " " + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.formatted_date = date.format(DateTimeFormatter.ofPattern("EE dd, MMM yyyy, HH:mm"));
        this.profilePicture = null;
        this.profileCircle = null;
    }

    public UserDTO(Long id, String firstName, String lastName, LocalDateTime date) {
        this.idUser = id;
        this.fullName = firstName + " " + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.formatted_date = date.format(DateTimeFormatter.ofPattern("EE dd, MMM yyyy, HH:mm"));
        this.profilePicture = null;
        this.profileCircle = null;
    }

    public UserDTO(Long idUser, String firstName, String lastName, LocalDateTime date, Image profilePicture) {
        this.idUser = idUser;
        this.fullName = firstName + " " + lastName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.formatted_date = date.format(DateTimeFormatter.ofPattern("EE dd, MMM yyyy, HH:mm"));
        this.profilePicture = profilePicture;
        this.profileCircle = new Circle();
        profileCircle.setRadius(30);
        profileCircle.setStrokeWidth(3);
        profileCircle.setStroke(Color.valueOf("0xdfbf86"));
        profileCircle.setFill(new ImagePattern(profilePicture));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getFormatted_date() {
        return formatted_date;
    }

    public void setFormatted_date(String formatted_date) {
        this.formatted_date = formatted_date;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Circle getProfileCircle() {
        return profileCircle;
    }

    public void setProfileCircle(Circle profileCircle) {
        this.profileCircle = profileCircle;
    }

    public Long getIdUser() {
        return idUser;
    }
}
