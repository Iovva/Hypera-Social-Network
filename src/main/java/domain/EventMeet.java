package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventMeet extends Entity<Long>{
    private String name;
    private String location;
    private String description;
    private LocalDate date;

    private List<Long> participants;
    private Long organizer;

    public EventMeet(Long id, String name, String location, LocalDate date, Long organizer) {
        super(id);
        this.name = name;
        this.location = location;
        this.date = date;
        this.organizer = organizer;

        this.description = "";
        this.participants = new ArrayList<>();
    }

    public EventMeet(String name, String location, LocalDate date, Long organizer) {
        super();
        this.name = name;
        this.location = location;
        this.date = date;
        this.organizer = organizer;

        this.description = "";
        this.participants = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    public List<Long> getParticipants() {
        return participants;
    }

    public Long getOrganizer() {
        return organizer;
    }

    public void addParticipant(long id){
        if (!participants.contains(id))
            participants.add(id);
    }

    public void removeParticipant(long id){
        participants.removeIf(x->x.equals(id));
    }

    public long numberOfParticipants(){
        return participants.size();
    }
}
