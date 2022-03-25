package repository.database;

import domain.Message;
import domain.User;
import domain.validator.Validator;
import exceptions.RepositoryException;
import repository.Repo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDbRepo implements Repo<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;

    public MessageDbRepo(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    public Message findOne(Long id, boolean getOriginalMessage) {
        if (id == null)
            throw new RepositoryException("Invalid ID!");
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps1 = connection.prepareStatement("SELECT * from messages where id = ?");
             PreparedStatement ps2 = connection.prepareStatement("SELECT * from users where id = ?")) {
            ps1.setLong(1, id);
            ResultSet resultSet = ps1.executeQuery();
            if (resultSet.next()) {
                long idFrom = resultSet.getLong("from_user");
                ps2.setLong(1, idFrom);
                ResultSet resultSet2 = ps2.executeQuery();
                User from = null;
                if (resultSet2.next()) {
                    long idUser = resultSet2.getLong("id");
                    String first_name = resultSet2.getString("first_name");
                    String last_name = resultSet2.getString("last_name");
                    from = new User(idUser, first_name, last_name);
                }

                String to = resultSet.getString("to_users");
                List<User> toList = new ArrayList<>();
                for (String el : to.split(";")) {
                    ps2.setLong(1, Long.parseLong(el));
                    resultSet2 = ps2.executeQuery();
                    if (resultSet2.next()) {
                        long idUser = resultSet2.getLong("id");
                        String first_name = resultSet2.getString("first_name");
                        String last_name = resultSet2.getString("last_name");
                        toList.add(new User(idUser, first_name, last_name));
                    }
                }

                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                long idReply = resultSet.getLong("reply");

                Message reply = null;
                if (idReply != 0 && getOriginalMessage) {
                    reply = findOne(idReply, false);
                }
                return new Message(id, from, toList, message, date, reply);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps1 = connection.prepareStatement("SELECT * from messages");
             PreparedStatement ps2 = connection.prepareStatement("SELECT * from users where id = ?");
             ResultSet resultSet = ps1.executeQuery()) {
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                long idFrom = resultSet.getLong("from_user");
                ps2.setLong(1, idFrom);
                ResultSet resultSet2 = ps2.executeQuery();
                User from = null;
                if (resultSet2.next()) {
                    long idUser = resultSet2.getLong("id");
                    String first_name = resultSet2.getString("first_name");
                    String last_name = resultSet2.getString("last_name");
                    from = new User(idUser, first_name, last_name);
                }

                String to = resultSet.getString("to_users");
                List<User> toList = new ArrayList<>();
                for (String el : to.split(";")) {
                    ps2.setLong(1, Long.parseLong(el));
                    resultSet2 = ps2.executeQuery();
                    if (resultSet2.next()) {
                        long idUser = resultSet2.getLong("id");
                        String first_name = resultSet2.getString("first_name");
                        String last_name = resultSet2.getString("last_name");
                        toList.add(new User(idUser, first_name, last_name));
                    }
                }


                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                long idReply = resultSet.getLong("reply");

                Message reply = null;
                if (idReply != 0) {
                    reply = findOne(idReply, true);
                }

                list.add(new Message(id, from, toList, message, date, reply));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Message save(Message entity) {
        if (entity==null)
            throw new RepositoryException("Invalid entity!");
        validator.validate(entity);
        String sql;
        if(entity.getReply() == null)
            sql = "insert into messages (message, date, from_user, to_users) values (?, ?, ?, ?)";
        else
            sql = "insert into messages (message, date, from_user, to_users, reply) values (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getMessage());
            ps.setTimestamp(2, Timestamp.valueOf(entity.getDate()));
            ps.setLong(3, entity.getFrom().getId());

            String to = "";
            for(User u : entity.getTo()){
                to += u.getId() + ";";
            }
            ps.setString(4, to);

            if(entity.getReply() != null)
                ps.setLong(5, entity.getReply().getId());
            if (ps.executeUpdate() == 0)
                return entity;
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }
}