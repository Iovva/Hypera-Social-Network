package repository.database;

import domain.EventMeet;
import domain.User;
import exceptions.RepositoryException;
import repository.Repo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EventDbRepo implements Repo<Long, EventMeet> {
    private final String url;
    private final String username;
    private final String password;

    public EventDbRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Iterable<EventMeet> findAll() {
        Set<EventMeet> events = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from events");
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String location = resultSet.getString("location");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Long organizer = resultSet.getLong("organizer");

                EventMeet event = new EventMeet(id,name,location,date,organizer);

                String description = resultSet.getString("description");
                if(description == null)
                    event.setDescription("");
                else
                    event.setDescription(description);

                String participant = resultSet.getString("participants");
                if(participant != null && !participant.equals(""))
                    for (String el : participant.split(";")) {
                        event.addParticipant(Integer.parseInt(el));
                    }

                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public EventMeet save(EventMeet entity) {
        if (entity == null)
            throw new RepositoryException("Invalid entity !");
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(
                     "insert into events (name, location, date, organizer, description, participants) values (?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getLocation());
            ps.setDate(3, Date.valueOf(entity.getDate()));
            ps.setLong(4,entity.getOrganizer());
            ps.setString(5, entity.getDescription());

            String participants = "";
            for(long part : entity.getParticipants())
                participants += part + ";";
            ps.setString(6, participants);

            if (ps.executeUpdate() == 0)
                return entity;
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public EventMeet delete(Long id) {
        if (id==null)
            throw new RepositoryException("Invalid ID!");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(
                     "delete from events where id = ? returning *")){
            ps.setLong(1, id);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Long idL = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String location = resultSet.getString("location");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Long organizer = resultSet.getLong("organizer");

                EventMeet deleted = new EventMeet(idL,name,location,date,organizer);

                String description = resultSet.getString("description");
                deleted.setDescription(description);

                String participants = resultSet.getString("participants");
                if(!participants.equals(""))
                    for (String el : participants.split(";")) {
                        deleted.addParticipant(Integer.parseInt(el));
                    }

                return deleted;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EventMeet update(EventMeet entity) {
        if(entity == null)
            throw new RepositoryException("Invalid entity!");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement
                     ("update events set name = ?, location = ?, date = ?, organizer = ?, description = ?, participants = ? where id = ?")) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getLocation());
            ps.setDate(3, Date.valueOf(entity.getDate()));
            ps.setLong(4,entity.getOrganizer());
            ps.setString(5, entity.getDescription());

            String participants = "";
            for(long part : entity.getParticipants())
                participants += part + ";";
            ps.setString(6, participants);

            ps.setLong(7, entity.getId());

            if (ps.executeUpdate() != 0)
                return null;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return entity;
    }
}
