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
    private static final String ADD_USER = "INSERT INTO USERS(LOGIN, EMAIL, NAME, BIRTHDAY) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE USERS SET " +
            "LOGIN = ?, EMAIL = ?, NAME = ?, BIRTHDAY = ?" +
            "WHERE ID = ?";
    private static final String GET_FRIENDS_IDS = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
            "WHERE REQUESTING_USER_ID = %d AND STATUS IS TRUE";
    private static final String GET_SUBSCRIBERS_IDS = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
            "WHERE REQUESTING_USER_ID = %d AND STATUS IS FALSE";
    private static final String DELETE_USER = "DELETE FROM USERS WHERE ID = ?";
    private static final String GET_USERS = "SELECT ID, LOGIN, EMAIL, NAME, BIRTHDAY FROM USERS";
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(ADD_USER, new String[]{"ID"});
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
    public User updateUser(User user) {
        jdbcTemplate.update(UPDATE_USER,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId());

        List<Long> friends = jdbcTemplate.query(String.format(GET_FRIENDS_IDS, user.getId()), this::mapRowToLong);
        Set<Long> friendsSet = new HashSet<>(friends);
        user.setFriends(friendsSet);

        List<Long> subscribers = jdbcTemplate.query(
                String.format(GET_SUBSCRIBERS_IDS, user.getId()), this::mapRowToLong);
        Set<Long> subscribersSet = new HashSet<>(subscribers);
        user.setSubscribers(subscribersSet);

        return user;
    }

    @Override
    public void deleteUser(long id) { //удаление пользователя
        jdbcTemplate.update(DELETE_USER, id);
    }

    @Override
    public List<User> getAllUsers() { //поиск всех пользователей
        return jdbcTemplate.query(GET_USERS, this::mapRowToUser);
    }

    @Override
    public Optional<User> getUserById(long id) {
        List<User> listUser = jdbcTemplate.query(GET_USERS, this::mapRowToUser);
        Map<Long, User> mapUser = new HashMap<>();
        for (User user : listUser) {
            mapUser.put(user.getId(), user);
        }
        if (mapUser.containsKey(id)) {
            User foundUser = mapUser.get(id);
            return Optional.of(foundUser);
        } else {
            return Optional.empty();
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User(resultSet.getLong("ID"),
                resultSet.getString("EMAIL"),
                resultSet.getString("LOGIN"),
                resultSet.getString("NAME"),
                resultSet.getDate("BIRTHDAY").toLocalDate(), null, null
        );

        List<Long> friends = jdbcTemplate.query(String.format(GET_FRIENDS_IDS, user.getId()), this::mapRowToLong);
        Set<Long> friendsSet = new HashSet<>(friends);
        user.setFriends(friendsSet);

        List<Long> subscribers = jdbcTemplate.query(
                String.format(GET_SUBSCRIBERS_IDS, user.getId()), this::mapRowToLong);
        Set<Long> subscribersSet = new HashSet<>(subscribers);
        user.setSubscribers(subscribersSet);

        return user;
    }

    private Long mapRowToLong(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("ID");
        return id;
    }
}
