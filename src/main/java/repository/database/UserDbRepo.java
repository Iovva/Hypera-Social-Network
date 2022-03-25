package repository.database;

import domain.User;
import domain.validator.Validator;
import exceptions.RepositoryException;
import repository.Repo;
import security.AES256;

import java.sql.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserDbRepo implements Repo<Long, User> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;
    private final AES256 encryptor;

    public UserDbRepo(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.encryptor = new AES256();
    }

    public boolean loginUser(User user){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from users where first_name = ? and last_name = ?")) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()){
                if (Objects.equals(resultSet.getString("password"), encryptor.encrypt(user.getPassword())))
                    return true;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public User findOne(Long id) {
        if (id==null)
            throw new RepositoryException("Invalid ID!");
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from users where id = ?")) {
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()){
                return new User(resultSet.getLong("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name")
                        //, resultSet.getString("password")
                );
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public User findOneByName(String firstName, String lastName){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from users where first_name = ? and last_name = ?")) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
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

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                //String password = resultSet.getString("password");
                User user = new User(id, firstName, lastName
                        //, password
                );
                users.add(user);
            }
            //return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User save(User entity) {
        if (entity==null)
            throw new RepositoryException("Invalid entity!");
        validator.validate(entity);
        if (findOneByName(entity.getFirstName(), entity.getLastName()) != null)
            throw new RepositoryException("User already exists!");
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(
                     "insert into users (first_name, last_name, password) values (?, ?, ?)")) {
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, encryptor.encrypt(entity.getPassword()));
            if (ps.executeUpdate() == 0)
                return entity;
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public User delete(Long id) {
        if (id==null)
            throw new RepositoryException("Invalid ID!");
        User deleted;
        deleted = findOne(id);
        if (deleted == null)
            return null;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(
                     "delete from users where id = ? ")){
            ps.setLong(1, id);
            ps.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return deleted;
    }

    @Override
    public User update(User entity) {
        if(entity == null)
            throw new RepositoryException("Invalid entity!");
        validator.validate(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("update users set first_name = ?, last_name = ?" +
                     //", password = ? " +
                     "where id = ?")) {
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            //ps.setString(3, entity.getPassword());
            ps.setLong(3, entity.getId());
            if (ps.executeUpdate() != 0)
                return null;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return entity;
    }
}