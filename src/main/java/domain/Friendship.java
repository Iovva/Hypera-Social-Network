package domain;

import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<User,User>> {

    LocalDateTime date;
    String status;
    //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * constructor for Friendship
     * @param id Tuple
     */


    public Friendship(Tuple<User, User> id, LocalDateTime date, String status) {
        super(id);
        this.date = date;
        this.status = status;
    }



    public Friendship(User leftId, User rightId, LocalDateTime date, String status){
        super();
        Tuple<User, User> id = new Tuple<>(leftId, rightId);
        setId(id);
        this.date = date;
        this.status = status;
    }

    public void setDate(LocalDateTime date){
        this.date = date;
    }

    /**
     * @return date when friendship was established
     */
    public LocalDateTime getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

