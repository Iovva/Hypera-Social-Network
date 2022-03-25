package controller;

import domain.MessageDTO;
import domain.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import java.util.List;

public class MessagesController extends ScreenController  {
    public TextArea typeingArea;
    public Button sendButton;

    // User selection
    @FXML
    public TableView<UserDTO> tableChats;

    @FXML
    public TableColumn<UserDTO, String> tableProfileColumn;
    @FXML
    public TableColumn<UserDTO, String> tableUserColumn;


    private UserDTO selectedChat;

    private ObservableList<UserDTO> users = FXCollections.observableArrayList();

    // Messages
    public TableView<MessageDTO> tableMessages;

    @FXML
    public TableColumn<MessageDTO,String> tableFromProfileColumn;
    @FXML
    public TableColumn<MessageDTO,String> tableFromMessageColumn;
    @FXML
    public TableColumn<MessageDTO,String> tableMeMessageColumn;
    @FXML
    public TableColumn<MessageDTO,String> tableMeProfileColumn;

    private MessageDTO selectedMessage;

    private ObservableList<MessageDTO> messages = FXCollections.observableArrayList();

    @FXML
    public Label labelFriendName;

    public Circle profilePictureFriend;

    private Long offset = 0L;

    @FXML
    public void initialize(){
        sendButton.setVisible(false);
        typeingArea.setVisible(false);
        tableMessages.setVisible(false);
        labelFriendName.setVisible(false);
        profilePictureFriend.setVisible(false);
        selectedChat = null;
        selectedMessage = null;

        tableUserColumn.setCellValueFactory(
                new PropertyValueFactory<UserDTO, String>("fullName")
        );
        tableProfileColumn.setCellValueFactory(
                new PropertyValueFactory<UserDTO, String>("profileCircle")
        );

        tableFromProfileColumn.setCellValueFactory(
                new PropertyValueFactory<MessageDTO, String>("fromCircle")
        );
        tableFromMessageColumn.setCellValueFactory(
                new PropertyValueFactory<MessageDTO, String>("fromString")
        );
        tableMeMessageColumn.setCellValueFactory(
                new PropertyValueFactory<MessageDTO, String>("myString")
        );
        tableMeProfileColumn.setCellValueFactory(
                new PropertyValueFactory<MessageDTO, String>("myCircle")
        );

        tableFromMessageColumn.setCellFactory(cv -> new TableCell<MessageDTO, String>(){
            @Override
            protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null)
                    setStyle("");
                else if (!item.equals("")) {
                    setStyle("-fx-background-color: #2b2b2b;" +
                            "-fx-background-radius: 0 30 30 30;" +
                            "-fx-border-radius: 0 30 30 30;" +
                            "-fx-border-color: black;" +
                            "-fx-border-width: 2;");
                }
                else{
                    setStyle("");
                }
                setText(item);
            }
        });

        tableMeMessageColumn.setCellFactory(cv -> new TableCell<MessageDTO, String>(){
            @Override
            protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null)
                    setStyle("");
                else if (!item.equals("")) {
                    setStyle("-fx-background-color: #5c1775;" +
                            "-fx-background-radius: 30 0 30 30;" +
                            "-fx-border-radius: 30 0 30 30;" +
                            "-fx-border-color: black;" +
                            "-fx-border-width: 2;");
                }
                else{
                    setStyle("");
                }
                setText(item);
            }
        });

        List<UserDTO> userDTOS = service.findFriendshipsWithDate(currentUser.getId(), true, 8L, offset);

        users.clear();
        users.addAll(userDTOS);
        tableChats.setItems(users);
    }

    @FXML
    private void update() {
        typeingArea.setVisible(true);
        sendButton.setVisible(true);
        tableMessages.setVisible(true);
        labelFriendName.setVisible(true);
        profilePictureFriend.setVisible(true);
        selectedMessage = null;
        typeingArea.clear();
        tableMessages.getSelectionModel().clearSelection();

        List<MessageDTO> messageDTOS = service.getConversationDTO(currentUser.getId(), selectedChat.getIdUser());

        messages.clear();
        messages.addAll(messageDTOS);
        tableMessages.setItems(messages);
        tableMessages.scrollTo(messages.size() - 1);
    }

    @FXML
    public void friendClicked(MouseEvent mouseEvent) {
        selectedChat = tableChats.getSelectionModel().getSelectedItem();

        if (selectedChat != null){
            profilePictureFriend.setVisible(true);
            profilePictureFriend.setFill(selectedChat.getProfileCircle().getFill());
            labelFriendName.setVisible(true);
            labelFriendName.setText(selectedChat.getFullName());
            update();
        }
    }

    @FXML
    public void messageClicked(MouseEvent mouseEvent) {
        if(selectedMessage == null) {
            selectedMessage = tableMessages.getSelectionModel().getSelectedItem();
        }
        else {
            tableMessages.getSelectionModel().clearSelection();
            selectedMessage = null;
        }
    }

    @FXML
    public void sendClicked(){
        String messageToBeSent = typeingArea.getText().strip();
        Long idReply;
        if(selectedMessage == null)
            idReply = 0L;
        else
            idReply = selectedMessage.getIdMessage();
        service.addMessage(currentUser.getId(), List.of(selectedChat.getIdUser()),messageToBeSent,idReply);
        update();
    }

    @FXML
    public void showNext(){

        if (tableChats.getItems().size() == 8)    {
            offset += 8;
            List<UserDTO> userDTOS = service.findFriendshipsWithDate(currentUser.getId(), true, 8L, offset);
            users.clear();
            users.addAll(userDTOS);
            tableChats.setItems(users);
        }
        else
            giveWarning("That's the end of your friends list!");
    }

    @FXML
    public void showPrev(){

        if (offset >= 8)    {
            offset -= 8;
            List<UserDTO> userDTOS = service.findFriendshipsWithDate(currentUser.getId(), true, 8L, offset);
            users.clear();
            users.addAll(userDTOS);
            tableChats.setItems(users);
        }
        else {
            giveWarning("That's the end of your friends list!");
        }
    }


}
