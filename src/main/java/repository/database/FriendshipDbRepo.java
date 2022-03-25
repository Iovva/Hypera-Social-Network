package repository.database;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validator.Validator;
import exceptions.RepositoryException;
import repository.Repo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FriendshipDbRepo implements Repo<Tuple<User, User>, Friendship> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;

    public FriendshipDbRepo(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    private User findUserById(Long id){
        if (id==null)
            throw new RepositoryException("Invalid ID!");
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from users where id = ?")) {
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()){
                return new User(resultSet.getLong("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public LocalDateTime getDateForFriendship(User user1, User user2){

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from friendships where left_id = ? and right_id = ? or left_id = ? and right_id = ?")) {
            ps.setLong(1, user1.getId());
            ps.setLong(2, user2.getId());
            ps.setLong(3, user2.getId());
            ps.setLong(4, user1.getId());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()){
                return resultSet.getTimestamp("date").toLocalDateTime();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getStatusForFriendship(User user1, User user2){

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from friendships where left_id = ? and right_id = ? or left_id = ? and right_id = ?")) {
            ps.setLong(1, user1.getId());
            ps.setLong(2, user2.getId());
            ps.setLong(3, user2.getId());
            ps.setLong(4, user1.getId());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("status");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {

                list.add( new Friendship(
                        findUserById(resultSet.getLong("left_id")),
                        findUserById(resultSet.getLong("right_id")),
                        resultSet.getTimestamp("date").toLocalDateTime(),
                        resultSet.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Iterable<Friendship> findAllPaged(long id, long limit, long offset) {
        List<Friendship> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from friendships where (left_id = ? or right_id = ?) and status = 'approved' ORDER BY date DESC LIMIT ? OFFSET ?");
        ) {

            ps.setLong(1, id);
            ps.setLong(2, id);
            ps.setLong(3, limit);
            ps.setLong(4, offset);

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getLong("left_id") == id || resultSet.getLong("right_id") == id)
                    list.add(new Friendship(
                            findUserById(resultSet.getLong("left_id")),
                            findUserById(resultSet.getLong("right_id")),
                            resultSet.getTimestamp("date").toLocalDateTime(),
                            resultSet.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Friendship save(Friendship entity) {
        if (entity == null)
            throw new RepositoryException("Invalid entity!");
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("insert into friendships (left_id, right_id, date, status) values (?, ?, ?, ?)");
             PreparedStatement ps2 = connection.prepareStatement("select count(*) from friendships where left_id = ? and right_id = ? or left_id = ? and right_id = ?")) {

            ps2.setLong(1, entity.getId().getLeft().getId());
            ps2.setLong(2, entity.getId().getRight().getId());
            ps2.setLong(3, entity.getId().getRight().getId());
            ps2.setLong(4, entity.getId().getLeft().getId());

            ResultSet resultSet = ps2.executeQuery();
            if(resultSet.next())
                if(resultSet.getInt(1) != 0)
                    return entity;

            ps.setLong(1, entity.getId().getLeft().getId());
            ps.setLong(2, entity.getId().getRight().getId());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            ps.setString(4, entity.getStatus());
            if (ps.executeUpdate() == 0)
                return entity;
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public Friendship delete(Tuple<User, User> id) {
        if (id==null)
            throw new RepositoryException("Invalid ID!");
        //checkInUsers(id.getLeft(), id.getRight());
        LocalDateTime date = getDateForFriendship(id.getLeft(), id.getRight());
        String status = getStatusForFriendship(id.getLeft(), id.getRight());
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(
                     "delete from friendships where left_id = ? and right_id = ? or left_id = ? and right_id = ?"))
        {
            ps.setLong(1, id.getLeft().getId());
            ps.setLong(2, id.getRight().getId());
            ps.setLong(3, id.getRight().getId());
            ps.setLong(4, id.getLeft().getId());
            if (ps.executeUpdate() != 0){
                return new Friendship(id.getLeft(), id.getRight(), date, status);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship update(Friendship entity) {
        if (entity==null)
            throw new RepositoryException("Invalid entity!");
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(
                     "update friendships set date = ?, status = ? where left_id = ? and right_id = ? or left_id = ? and right_id = ?")) {

            ps.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            ps.setString(2, entity.getStatus());
            ps.setLong(3, entity.getId().getLeft().getId());
            ps.setLong(4,entity.getId().getRight().getId());
            ps.setLong(5,entity.getId().getRight().getId());
            ps.setLong(6,entity.getId().getLeft().getId());
            if (ps.executeUpdate() == 0)
                return null;
        } catch (SQLException e) {
            return null;
        }
        return entity;
    }
}