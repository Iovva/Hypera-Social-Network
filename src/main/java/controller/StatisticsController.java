package controller;

import domain.Message;
import domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class StatisticsController extends ScreenController  {

    @FXML
    DatePicker datePicker1;

    @FXML
    DatePicker datePicker2;

    @FXML
    TextField friendField;

    private void exportReport2(List<Message> messages, User friend) throws IOException {

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        PDPageContentStream contents = new PDPageContentStream(doc, page);
        contents.beginText();

        PDFont font = PDType1Font.HELVETICA_BOLD;
        contents.setFont(font, 22);

        contents.newLineAtOffset(50, 700);
        contents.showText("Messages with " + friend.getFirstName() + " " + friend.getLastName());

        font = PDType1Font.TIMES_ROMAN;
        contents.setFont(font, 16);
        contents.setLeading(29f);

        contents.newLine();
        contents.newLine();

        exportMessages(messages, doc, contents, font);

        String fileName; //= currentUser.getFirstName() + " " +
//                currentUser.getLastName() + " " +
//                "messages with " +
//                friend.getFirstName() + " " +
//                friend.getLastName() +
//                ".pdf";

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("PDFs", "*.pdf");
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(imageFilter);
        File file = chooser.showSaveDialog(friendField.getScene().getWindow());
        if (file != null) {
            System.out.println(file.getAbsolutePath());
            fileName = file.getAbsolutePath();
            doc.save(fileName);
            giveConfirmation("Document has been exported !");
        }
        else
            giveWarning("Error at export !");

        doc.close();
    }

    private void exportMessages(List<Message> messages, PDDocument doc, PDPageContentStream contents, PDFont font) throws IOException {
        PDPage page;
        int messageCounter = 0;

        for (Message m : messages){
            if (messageCounter == 7){

                messageCounter = 0;

                contents.endText();
                contents.close();

                page = new PDPage();
                doc.addPage(page);

                contents = new PDPageContentStream(doc, page);
                contents.beginText();
                contents.setFont(font, 16);
                contents.newLineAtOffset(50, 700);
                contents.setLeading(29f);
            }

            contents.showText(m.getFrom().getFirstName() + " " + m.getFrom().getLastName());
            contents.newLine();

            contents.showText(m.getMessage());
            contents.newLine();
            contents.newLine();

            messageCounter++;
        }

        contents.endText();
        contents.close();
    }

    @FXML
    protected void reportMessagesClick() throws IOException {

        try {
            LocalDateTime date1 = datePicker1.getValue().atTime(LocalTime.parse("00:00"));
            LocalDateTime date2 = datePicker2.getValue().atTime(LocalTime.parse("23:59"));

            if (date1.isAfter(date2)){
                giveWarning("Second date should be after the first date!");
                return;
            }
            String[] nameSplit = friendField.getText().split("\\s+");
            User tryUser = service.findUserByName(nameSplit[0], nameSplit[1]);

            if (tryUser != null) {

                List<Message> messages = service.getConversationInDate
                        (currentUser.getId(), tryUser.getId(), true, false, date1, date2);

                exportReport2(messages, tryUser);
            }
        }
        catch (NullPointerException e){
            giveWarning("Invalid date!");
        }
        catch (IndexOutOfBoundsException e){
            giveWarning("Please enter the friend's full name!");
        }
    }

    private void exportReport1(List<User> usersAdded, List<Message> messages) throws IOException {


        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        PDPageContentStream contents = new PDPageContentStream(doc, page);
        contents.beginText();

        PDFont font = PDType1Font.HELVETICA_BOLD;
        contents.setFont(font, 22);

        contents.newLineAtOffset(50, 700);
        contents.showText("New friends + Messages");


        font = PDType1Font.TIMES_ROMAN;
        contents.setFont(font, 16);
        contents.setLeading(29f);

        contents.newLine();
        contents.newLine();

        contents.showText("Users added:");
        contents.newLine();

        StringBuilder users = new StringBuilder();
        for (User u : usersAdded) {
            users.append(u.getFirstName()).append(" ").append(u.getLastName()).append(", ");
        }

        contents.showText(users.toString());

        contents.newLine();
        contents.newLine();

        exportMessages(messages, doc, contents, font);

        String fileName;// = currentUser.getFirstName() + " " +
                //currentUser.getLastName() + " " +
                //"Messages and Friends.pdf";

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("PDFs", "*.pdf");
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(imageFilter);
        File file = chooser.showSaveDialog(friendField.getScene().getWindow());
        if (file != null) {
            System.out.println(file.getAbsolutePath());
            fileName = file.getAbsolutePath();
            doc.save(fileName);
            giveConfirmation("Document has been exported !");
        }
        else
            giveWarning("Error at export !");

        doc.close();
    }

    @FXML
    protected void reportFriendsMessagesClick() throws IOException {

        try {
            LocalDateTime date1 = datePicker1.getValue().atTime(LocalTime.parse("00:00"));
            LocalDateTime date2 = datePicker2.getValue().atTime(LocalTime.parse("23:59"));

            if (date1.isAfter(date2)) {
                giveWarning("Second date should be after the first date!");
                return;
            }

            List<User> usersAdded = service.getUsersAddedInDate(currentUser.getId(), date1, date2);

            List<Message> messages = service.getIncomingMessagesInDate
                    (currentUser.getId(), true, date1, date2);

            exportReport1(usersAdded, messages);

        }
        catch (NullPointerException e){
            giveWarning("Invalid date!");
        }
        catch (IndexOutOfBoundsException e){
            giveWarning("Please enter the friend's full name!");
        }
    }
}
