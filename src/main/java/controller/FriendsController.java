package controller;

import domain.*;
import exceptions.ValidatorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.time.LocalDateTime;
import java.util.List;

public class FriendsController extends ScreenController  {
    User searchedUser = null;

    public Button acceptButtonID;
    public Button denyButtonID;
    public Button cancelButtonID;
    public Button removeButtonID;

    public TextField searchField;

    public TableView<FriendshipDTO> sentRequestsTable;
    public TableColumn<FriendshipDTO, String> toColumn;
    public TableColumn<FriendshipDTO, String> toStatusColumn;

    public TableView<FriendshipDTO> receivedRequestsTable;
    public TableColumn<FriendshipDTO, String>  fromColumn;
    public TableColumn<FriendshipDTO, String>  fromStatusColumn;

    public TableView<UserDTO> friendsView;
    public TableColumn<UserDTO, String> friendsPictureColumn;
    public TableColumn<UserDTO, String> friendsFirstNameColumn;
    public TableColumn<UserDTO, String> friendsLastNameColumn;
    public TableColumn<UserDTO, String> friendsDateColumn;

    public Label statusFound;

    ObservableList<UserDTO> friends = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> fromRequests = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> toRequests = FXCollections.observableArrayList();

    private void refreshFriends(){
        List<UserDTO> userDTOS = service.findFriendshipsWithDate(currentUser.getId(), false, 0, 0);

        friends.clear();
        friends.addAll(userDTOS);

        friendsView.setItems(friends);
        friendsView.getSelectionModel().clearSelection();
    }

    private void refreshFrom(){
        List<FriendshipDTO> friendshipDTOS = service.getReceivedFriendRequests(currentUser.getId());

        fromRequests.clear();
        fromRequests.addAll(friendshipDTOS);

        receivedRequestsTable.setItems(fromRequests);
        receivedRequestsTable.getSelectionModel().clearSelection();
    }

    private void refreshTo(){
        List<FriendshipDTO> friendshipDTOS = service.getSentFriendRequests(currentUser.getId());

        toRequests.clear();
        toRequests.addAll(friendshipDTOS);

        sentRequestsTable.setItems(toRequests);
        sentRequestsTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void initialize(){
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            List<String> nameSplit = List.of(newValue.split("\\s+"));
            //System.out.println(nameSplit);
            if(nameSplit.size() >=2) {
                searchedUser = service.findUserByName(nameSplit.get(0), nameSplit.get(1));

                if (searchedUser == null) {
                    statusFound.setText("Not found !");
                    statusFound.setVisible(true);
                    statusFound.setStyle("-fx-text-fill:red");
                } else {
                    statusFound.setText("Found !");
                    statusFound.setVisible(true);
                    statusFound.setStyle("-fx-text-fill:green");
                }
            }
            else {
                statusFound.setText("Not found !");
                statusFound.setVisible(true);
                statusFound.setStyle("-fx-text-fill:red");
                searchedUser = null;
            }
            //System.out.println("textfield changed from " + oldValue + " to " + newValue);
        });

        statusFound.setVisible(false);

        friendsPictureColumn.setCellValueFactory(
                new PropertyValueFactory<UserDTO, String>("profileCircle")
        );
        friendsFirstNameColumn.setCellValueFactory(
                new PropertyValueFactory<UserDTO, String>("firstName")
        );
        friendsLastNameColumn.setCellValueFactory(
                new PropertyValueFactory<UserDTO, String>("lastName")
        );
        friendsDateColumn.setCellValueFactory(
                new PropertyValueFactory<UserDTO, String>("formatted_date")
        );

        refreshFriends();

        fromColumn.setCellValueFactory(
                new PropertyValueFactory<FriendshipDTO, String>("fullName")
        );
        fromStatusColumn.setCellValueFactory(
                new PropertyValueFactory<FriendshipDTO, String>("status")
        );

        refreshFrom();

        toColumn.setCellValueFactory(
                new PropertyValueFactory<FriendshipDTO, String>("fullName")
        );
        toStatusColumn.setCellValueFactory(
                new PropertyValueFactory<FriendshipDTO, String>("status")
        );

        refreshTo();

        oldButtonStyle = cancelButtonID.getStyle();
    }


    public void addButton(ActionEvent actionEvent) {
        if(searchField.getText().equals("")){
            searchField.clear();
            statusFound.setText("Enter the full name !");
            statusFound.setVisible(true);
            statusFound.setStyle("-fx-text-fill:red");
            searchedUser = null;
            return;
        }

        if(statusFound.getText().equals("Found !")) {
            try {
                Friendship returnValue = service.sendFriendRequest(currentUser.getId(), searchedUser.getId(), LocalDateTime.now());
                if (returnValue != null) {
                    String status = service.getStatus(currentUser.getId(), searchedUser.getId());
                    if (status.equals("approved")) {
                        giveWarning("This user is already in your friend list !");
                        searchedUser = null;
                        return;
                    } else if (status.equals("pending")) {
                        giveWarning("Friend request already sent !");
                        searchedUser = null;
                        return;
                    } else {
                        service.removeFriendship(currentUser.getId(), searchedUser.getId());
                        service.sendFriendRequest(currentUser.getId(), searchedUser.getId(), LocalDateTime.now());
                    }
                }
            }
            catch (ValidatorException ve){
                giveWarning(ve.getMessage());
            }
            searchedUser = null;
            refreshTo();
            refreshFrom();
        }
        searchField.clear();
        statusFound.setVisible(false);
    }

    public void removeButton(ActionEvent actionEvent) {
        UserDTO userToRemove = friendsView.getSelectionModel().getSelectedItem();

        if(userToRemove != null) {
            User user = service.findUserByName(userToRemove.getFirstName(), userToRemove.getLastName());

            service.removeFriendship(currentUser.getId(), user.getId());
            removeButtonID.setStyle(oldButtonStyle);
            searchField.clear();
            refreshFriends();
            refreshTo();
            refreshFrom();
        }
        else
            giveWarning("Select the user you want to remove !");
    }

    public void acceptButton(ActionEvent actionEvent) {
        FriendshipDTO friendshipReceived = receivedRequestsTable.getSelectionModel().getSelectedItem();

        if(friendshipReceived != null) {
            if(!friendshipReceived.getStatus().equals("pending")){
                giveWarning("Friendship must be pending !");
                return;
            }

            service.respondFriendship(currentUser.getId(), friendshipReceived.getLeftId(),"approved");
            acceptButtonID.setStyle(oldButtonStyle);
            denyButtonID.setStyle(oldButtonStyle);
            searchField.clear();
            refreshFriends();
            refreshFrom();
        }
        else
            giveWarning("Select the friend request you want to accept !");
    }

    public void denyButton(ActionEvent actionEvent) {
        FriendshipDTO friendshipReceived = receivedRequestsTable.getSelectionModel().getSelectedItem();

        if(friendshipReceived != null) {
            if(!friendshipReceived.getStatus().equals("pending")){
                giveWarning("Friendship must be pending !");
                return;
            }

            service.respondFriendship(currentUser.getId(), friendshipReceived.getLeftId(),"rejected");
            acceptButtonID.setStyle(oldButtonStyle);
            denyButtonID.setStyle(oldButtonStyle);
            searchField.clear();
            refreshFriends();
            refreshFrom();
        }
        else
            giveWarning("Select the friend request you want to reject !");
    }

    public void cancelButton(ActionEvent actionEvent) {
        FriendshipDTO friendshipToCancel = sentRequestsTable.getSelectionModel().getSelectedItem();

        if(friendshipToCancel != null) {
            if(!friendshipToCancel.getStatus().equals("pending")){
                giveWarning("Friendship must be pending !");
                return;
            }
            service.removeFriendship(currentUser.getId(), friendshipToCancel.getRightId());
            cancelButtonID.setStyle(oldButtonStyle);
            searchField.clear();
            refreshTo();
        }
        else
            giveWarning("Select the friend request you want to cancel !");
    }

    String oldButtonStyle;
    Object oldSelection;
    public void toRequestsClicked(MouseEvent mouseEvent){
        if(sentRequestsTable.getSelectionModel().getSelectedItem() != null)
            if(sentRequestsTable.getSelectionModel().getSelectedItem() != oldSelection) {
                oldSelection = sentRequestsTable.getSelectionModel().getSelectedItem();
                cancelButtonID.setStyle("-fx-background-color: purple; -fx-background-radius: 30");

                friendsView.getSelectionModel().clearSelection();
                receivedRequestsTable.getSelectionModel().clearSelection();
                acceptButtonID.setStyle(oldButtonStyle);
                denyButtonID.setStyle(oldButtonStyle);
                removeButtonID.setStyle(oldButtonStyle);
            }
            else{
                cancelButtonID.setStyle(oldButtonStyle);
                oldSelection = null;
                sentRequestsTable.getSelectionModel().clearSelection();
            }
    }

    public void fromRequestsClicked(MouseEvent mouseEvent){
        if(receivedRequestsTable.getSelectionModel().getSelectedItem() != null)
            if(receivedRequestsTable.getSelectionModel().getSelectedItem() != oldSelection) {
                oldSelection = receivedRequestsTable.getSelectionModel().getSelectedItem();
                acceptButtonID.setStyle("-fx-background-color: purple; -fx-background-radius: 30");
                denyButtonID.setStyle("-fx-background-color: purple; -fx-background-radius: 30");


                friendsView.getSelectionModel().clearSelection();
                sentRequestsTable.getSelectionModel().clearSelection();
                cancelButtonID.setStyle(oldButtonStyle);
                removeButtonID.setStyle(oldButtonStyle);
            }
            else{
                acceptButtonID.setStyle(oldButtonStyle);
                denyButtonID.setStyle(oldButtonStyle);
                oldSelection = null;
                receivedRequestsTable.getSelectionModel().clearSelection();
            }
    }

    public void friendsClicked(MouseEvent mouseEvent){
        if(friendsView.getSelectionModel().getSelectedItem() != null)
            if(friendsView.getSelectionModel().getSelectedItem() != oldSelection) {
                oldSelection = friendsView.getSelectionModel().getSelectedItem();
                removeButtonID.setStyle("-fx-background-color: purple; -fx-background-radius: 30");

                sentRequestsTable.getSelectionModel().clearSelection();
                receivedRequestsTable.getSelectionModel().clearSelection();
                sentRequestsTable.getSelectionModel().clearSelection();
                cancelButtonID.setStyle(oldButtonStyle);
                acceptButtonID.setStyle(oldButtonStyle);
                denyButtonID.setStyle(oldButtonStyle);
            }
            else{
                removeButtonID.setStyle(oldButtonStyle);
                oldSelection = null;
                friendsView.getSelectionModel().clearSelection();
            }
    }
}
