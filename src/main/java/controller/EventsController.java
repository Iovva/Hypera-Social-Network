package controller;

import domain.EventMeet;
import domain.User;
import domain.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventsController extends ScreenController  {
    public TableView<EventMeet> eventsTable;
    public TableColumn<EventMeet, String> eventsNameColumn;
    public TableColumn<EventMeet, String> eventsDateColumn;

    public TableView<UserDTO> participantsTable;
    public TableColumn<UserDTO, String> participantsNameColumn;

    public TextArea nameField;
    public TextArea descriptionField;
    public TextArea locationField;
    public DatePicker dateField;
    public TextArea organizerField;

    public Button createEventButton;
    public Button subscribeButton;

    ObservableList<EventMeet> events = FXCollections.observableArrayList();;
    ObservableList<UserDTO> participants = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        participantsTable.setVisible(false);
        participantsTable.setSelectionModel(null);
        subscribeButton.setVisible(false);
        organizerField.setEditable(false);
        organizerField.setDisable(true);
        organizerField.setText(currentUser.getFirstName() + " " + currentUser.getLastName());

        eventsNameColumn.setCellValueFactory(
                new PropertyValueFactory<EventMeet, String>("name")
        );
        eventsDateColumn.setCellValueFactory(
                new PropertyValueFactory<EventMeet, String>("date")
        );

        participantsNameColumn.setCellValueFactory(
                new PropertyValueFactory<UserDTO, String>("fullName")
        );

        events.addAll(service.getEvents());
        eventsTable.setItems(events);
        participantsTable.setItems(participants);
    }

    public void createEventButtonClicked(ActionEvent actionEvent) {
        String name = nameField.getText();
        String location = locationField.getText();
        String description = descriptionField.getText();
        LocalDate date = dateField.getValue();

        String error = "";
        if(name.strip().equals(""))
            error += "Invalid name !\n";
        if(location.strip().equals(""))
            error += "Invalid location !\n";
        if(date == null || date.isBefore(LocalDate.now()))
            error += "Invalid date !\n";

        if(!error.isEmpty()){
            giveWarning(error);
            return;
        }
        EventMeet event = new EventMeet(name,location,date, currentUser.getId());
        event.setDescription(description);

        service.addEvent(event);

        nameField.clear();
        locationField.clear();
        descriptionField.clear();
        dateField.getEditor().clear();

        updateEvents();
    }

    public void subscribeButtonClicked(ActionEvent actionEvent) {
        if(!subscribeButton.isDisable()) {
            EventMeet seletion = eventsTable.getSelectionModel().getSelectedItem();
            if (subscribeButton.getText().equals("Subscribe")) {
                seletion.addParticipant(currentUser.getId());
                subscribeButton.setText("Unsubscribe");
            } else {
                seletion.removeParticipant(currentUser.getId());
                subscribeButton.setText("Subscribe");
            }
            updateParticipants(seletion);
        }
    }

    private void updateEvents(){
        events.clear();
        events.addAll(service.getEvents());

        eventsTable.getSelectionModel().clearSelection();
    }

    private void updateParticipants(EventMeet eventSelected){
        participants.clear();
        for(Long id : eventSelected.getParticipants()) {
            User user = service.findUser(id);
            participants.add(new UserDTO(user.getId(), user.getFirstName(),user.getLastName(), LocalDateTime.now()));
        }
        participants.addAll();
    }

    EventMeet oldSelection;
    public void eventsTableClicked(MouseEvent mouseEvent) {
        if(eventsTable.getSelectionModel().getSelectedItem() != null) {
            EventMeet seletion = eventsTable.getSelectionModel().getSelectedItem();
            if (seletion != oldSelection) {
                oldSelection = eventsTable.getSelectionModel().getSelectedItem();
                participantsTable.setVisible(true);
                
                nameField.setEditable(false);
                nameField.setDisable(true);
                locationField.setEditable(false);
                locationField.setDisable(true);
                descriptionField.setEditable(false);
                descriptionField.setDisable(true);
                
                nameField.setText(seletion.getName());
                locationField.setText(seletion.getLocation());
                descriptionField.setText(seletion.getDescription());
                dateField.setEditable(false);
                dateField.setDisable(true);
                dateField.getEditor().setText(seletion.getDate().toString());
                User org = service.findUser(seletion.getOrganizer());
                organizerField.setText(org.getFirstName() + " " + org.getLastName());

                updateParticipants(seletion);

                createEventButton.setVisible(false);
                subscribeButton.setVisible(true);

                if(seletion.getOrganizer().equals(currentUser.getId()))
                    subscribeButton.setVisible(false);

                if (seletion.getParticipants().contains(currentUser.getId()))
                    subscribeButton.setText("Unsubscribe");
                else
                    subscribeButton.setText("Subscribe");
            } else {
                participantsTable.setVisible(false);
                oldSelection = null;
                subscribeButton.setVisible(false);
                createEventButton.setVisible(true);

                nameField.setEditable(true);
                nameField.setDisable(false);
                locationField.setEditable(true);
                locationField.setDisable(false);
                descriptionField.setEditable(true);
                descriptionField.setDisable(false);
                dateField.setEditable(true);
                dateField.setDisable(false);
                organizerField.setText(currentUser.getFirstName() + " " + currentUser.getLastName());

                participants.clear();

                nameField.clear();
                locationField.clear();
                descriptionField.clear();
                dateField.getEditor().clear();

                eventsTable.getSelectionModel().clearSelection();
            }
        }
    }
}
