package ru.yandex.practicum.filmorate.service;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component("userDbService")
@Primary
public class UserDbService implements UserService {
    private final JdbcTemplate jdbcTemplate;

    public UserDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long newFriendId, long targetUserId) {
        String sqlQuery = "INSERT INTO FRIENDS(REQUESTING_USER_ID, REQUESTED_USER_ID, STATUS) " +
                "VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery2 = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
                "WHERE REQUESTING_USER_ID = " + targetUserId + " AND STATUS IS TRUE";
        ArrayList<Long> friends = (ArrayList<Long>) jdbcTemplate.query(sqlQuery2, this::mapRowToLong);
        if (friends.contains(newFriendId)) {
            return;
        }

        String sqlQuery3 = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
                "WHERE REQUESTING_USER_ID = " + targetUserId + " AND STATUS IS FALSE";
        ArrayList<Long> subscribers = (ArrayList<Long>) jdbcTemplate.query(sqlQuery3, this::mapRowToLong);
        if (subscribers.contains(newFriendId)) {
            String sqlQuery4 = "SELECT ID FROM FRIENDS " +
                    "WHERE REQUESTING_USER_ID = " + targetUserId +
                    " AND REQUESTED_USER_ID = " + newFriendId + " AND STATUS IS FALSE";
            ArrayList<Long> ids = (ArrayList<Long>) jdbcTemplate.query(sqlQuery4, this::mapRowToLong);
            if (ids.size() == 1) {
                Long friends_id = ids.get(0);
                String sqlQuery5 = "UPDATE FRIENDS SET STATUS = ? WHERE ID = " + friends_id;
                jdbcTemplate.update(sqlQuery5,
                        true);
            }
            return;
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            stmt.setLong(1, targetUserId);
            stmt.setLong(2, newFriendId);
            stmt.setBoolean(3, true);
            return stmt;
        }, keyHolder);

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            stmt.setLong(1, newFriendId);
            stmt.setLong(2, targetUserId);
            stmt.setBoolean(3, false);
            return stmt;
        }, keyHolder);
    }

    @Override
    public boolean deleteFriend(long friendUserId, long targetUserId) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE REQUESTING_USER_ID = ? AND REQUESTED_USER_ID = ? ";
        return jdbcTemplate.update(sqlQuery, targetUserId, friendUserId) > jdbcTemplate.update(sqlQuery, friendUserId, targetUserId);
    }

    @Override
    public Set<User> findUserFriends(long targetUserId) {
        String sqlQuery = "SELECT ID, LOGIN, EMAIL, NAME, BIRTHDAY " +
                "FROM USERS WHERE ID IN (SELECT REQUESTED_USER_ID FROM FRIENDS " +
                "WHERE REQUESTING_USER_ID = " + targetUserId + " AND STATUS IS TRUE)";

        ArrayList<User> arrayFriends = (ArrayList<User>) jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        return new HashSet<>(arrayFriends);
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

    private Long mapRowToLong(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("ID");
        return id;
    }

    @Override
    public Set<User> findMutualFriends(long targetUser1Id, long targetUser2Id) {
        String sqlQuery = "SELECT USERS.ID, USERS.LOGIN, USERS.EMAIL, USERS.NAME, USERS.BIRTHDAY " +
                "FROM USERS WHERE USERS.ID IN(SELECT REQUESTED_USER_ID " +
                "FROM (SELECT REQUESTED_USER_ID FROM FRIENDS " +
                "WHERE REQUESTING_USER_ID = " + targetUser1Id + " AND STATUS IS TRUE) " +
                "WHERE REQUESTED_USER_ID INTERSECT (SELECT REQUESTED_USER_ID FROM FRIENDS " +
                "WHERE REQUESTING_USER_ID = " + targetUser2Id + " AND STATUS IS TRUE))";

        ArrayList<User> arrayFriends = (ArrayList<User>) jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        return new HashSet<>(arrayFriends);
    }
}
