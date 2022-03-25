package domain;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.time.LocalDateTime;

public class MessageDTO {
    private Message fromMessage;
    private Message myMessage;

    private LocalDateTime messageDate;

    private String fromString;
    private Circle fromCircle;

    private String myString;
    private Circle myCircle;

    private String replyString;

    private static String formatMessage(String stringToFormat) {
        int i = 0;
        String formatted = "";
        for (char c : stringToFormat.toCharArray()) {
            i++;
            if (i >= 17 && (c == ' ' || c == '\n') || i == 23) {
                i = 0;
                formatted += '\n';
                if(c != ' ' && c != '\n')
                    formatted += c;
                continue;
            }
            formatted += c;
        }
        return formatted;
    }

    private void setCircleImage(Circle circle, Image image){
        circle.setRadius(30);
        circle.setStrokeWidth(3);
        circle.setStroke(Color.valueOf("0xdfbf86"));
        circle.setFill(new ImagePattern(image));
    }

    public MessageDTO(Image fromImage, Message fromMessage, Message myMessage, Image myImage, String replyString) {
        this.fromMessage = fromMessage;
        this.myMessage = myMessage;

        this.fromString = "";//fromMessage.getMessage();
        this.myString = "";//myMessage.getMessage();
        this.replyString = replyString;

        this.fromCircle = new Circle();
        if(fromMessage.getFrom() != null)
            setCircleImage(fromCircle,fromImage);

        this.myCircle = new Circle();
        if(myMessage.getFrom() != null)
            setCircleImage(myCircle,myImage);

        if(fromMessage.getTo() != null){
            this.messageDate = fromMessage.getDate();
            this.fromString = formatMessage(replyString + fromMessage.getMessage());
//            this.fromString = fromMessage.getMessage();
        }
        else if(myMessage.getTo() != null) {
            this.messageDate = myMessage.getDate();
            this.myString = formatMessage(replyString + myMessage.getMessage());
//            this.myString = myMessage.getMessage();
        }
        else
            this.messageDate = null;
    }

    public Long getIdMessage(){
        if(fromString.equals(""))
            return myMessage.getId();
        else
            return fromMessage.getId();
    }

    public Circle getFromCircle() {
        return fromCircle;
    }

    public String getFromString() {
        return fromString;
    }

    public String getMyString() {
        return myString;
    }

    public Circle getMyCircle() {
        return myCircle;
    }

    public LocalDateTime getMessageDate() {
        return messageDate;
    }
}
