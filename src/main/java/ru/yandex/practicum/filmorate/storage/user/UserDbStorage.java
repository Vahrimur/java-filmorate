package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("userDbStorage")
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) { //добавление пользователя
        String sqlQuery = "INSERT INTO USERS(LOGIN, EMAIL, NAME, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getName());
            stmt.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public boolean deleteUser(User user) { //удаление пользователя
        String sqlQuery = "DELETE FROM USERS WHERE ID = ?";
        return jdbcTemplate.update(sqlQuery, user.getId()) > 0;
    }

    @Override
    public User updateUser(User user) { //модификация пользователя
        String sqlQuery1 = "UPDATE USERS SET " +
                "LOGIN = ?, EMAIL = ?, NAME = ?, BIRTHDAY = ?" +
                "WHERE ID = ?";
        jdbcTemplate.update(sqlQuery1,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId());

        String sqlQuery2 = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
                "WHERE REQUESTING_USER_ID = " + user.getId() + " AND STATUS IS TRUE";
        ArrayList<Long> friends = (ArrayList<Long>) jdbcTemplate.query(sqlQuery2, this::mapRowToLong);
        Set<Long> friendsSet = new HashSet<>(friends);
        user.setFriends(friendsSet);

        String sqlQuery3 = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
                "WHERE REQUESTING_USER_ID = " + user.getId() + " AND STATUS IS FALSE";
        ArrayList<Long> subscribers = (ArrayList<Long>) jdbcTemplate.query(sqlQuery3, this::mapRowToLong);
        Set<Long> subscribersSet = new HashSet<>(subscribers);
        user.setSubscribers(subscribersSet);
        return user;
    }

    private Long mapRowToLong(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("ID");
        return id;
    }

    @Override
    public Collection<User> findAllUsers() { //поиск всех пользователей
        String sqlQuery = "SELECT ID, LOGIN, EMAIL, NAME, BIRTHDAY FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User(resultSet.getLong("ID"),
                resultSet.getString("EMAIL"),
                resultSet.getString("LOGIN"),
                resultSet.getString("NAME"),
                resultSet.getDate("BIRTHDAY").toLocalDate(), null, null
        );

        String sqlQuery2 = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
                "WHERE REQUESTING_USER_ID = " + user.getId() + " AND STATUS IS TRUE";
        ArrayList<Long> friends = (ArrayList<Long>) jdbcTemplate.query(sqlQuery2, this::mapRowToLong);
        Set<Long> friendsSet = new HashSet<>(friends);
        user.setFriends(friendsSet);

        String sqlQuery3 = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
                "WHERE REQUESTING_USER_ID = " + user.getId() + " AND STATUS IS FALSE";
        ArrayList<Long> subscribers = (ArrayList<Long>) jdbcTemplate.query(sqlQuery3, this::mapRowToLong);
        Set<Long> subscribersSet = new HashSet<>(subscribers);
        user.setSubscribers(subscribersSet);
        return user;
    }

    @Override
    public Map<Long, User> getUsers() {
        String sqlQuery = "SELECT ID, LOGIN, EMAIL, NAME, BIRTHDAY FROM USERS";
        List<User> listUser = jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        Map<Long, User> mapUser = new HashMap<>();
        for (User user : listUser) {
            mapUser.put(user.getId(), user);
        }
        return mapUser;
    }
}
