package services;

import daos.UserDao;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserService {
    private UserDao userDao;

    public UserService(Connection connection) {
        this.userDao = new UserDao(connection);
    }

    public void registerUser(User user) throws SQLException {
        userDao.createUser(user);
    }

    public User login(String username, String password) throws SQLException {
        User user = userDao.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserByUsername (String username) throws SQLException {
        return userDao.getUserByUsername(username);
    }



    public void updateUser(User user) throws SQLException {
        userDao.updateUser(user);
    }

}