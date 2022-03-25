package domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    User from;
    List<User> to;
    String message;
    LocalDateTime date;
    Message reply;

    public Message(Long id, User from, List<User> to, String message, LocalDateTime date, Message reply){
        super(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    public Message(User from, List<User> to, String message, LocalDateTime date, Message reply){
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }


}
