package ru.yandex.practicum.filmorate.service.friends;

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
import java.util.List;
import java.util.Set;

@Component
@Primary
public class FriendsDbService implements FriendsService {
    private static final String ADD_FRIEND = "INSERT INTO FRIENDS(REQUESTING_USER_ID, REQUESTED_USER_ID, STATUS) " +
            "VALUES (?, ?, ?)";
    private static final String GET_FRIENDS_IDS = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
            "WHERE REQUESTING_USER_ID = %d AND STATUS IS TRUE";
    private static final String GET_SUBSCRIBERS_IDS = "SELECT REQUESTED_USER_ID AS ID FROM FRIENDS " +
            "WHERE REQUESTING_USER_ID = %d AND STATUS IS FALSE";
    private static final String SELECT_FALSE_FRIEND_ID = "SELECT ID FROM FRIENDS  WHERE REQUESTING_USER_ID = %d " +
            "AND REQUESTED_USER_ID = %d AND STATUS IS FALSE;";
    private static final String UPDATE_STATUS_TRUE = "UPDATE FRIENDS SET STATUS = ? WHERE ID = %d";
    private static final String DELETE_FRIEND = "DELETE FROM FRIENDS WHERE REQUESTING_USER_ID = ? " +
            "AND REQUESTED_USER_ID = ? ";
    private static final String FIND_FRIENDS = "SELECT ID, LOGIN, EMAIL, NAME, BIRTHDAY " +
            "FROM USERS WHERE ID IN (SELECT REQUESTED_USER_ID FROM FRIENDS " +
            "WHERE REQUESTING_USER_ID = %d AND STATUS IS TRUE)";
    private static final String FIND_MUTUAL_FRIENDS = "SELECT USERS.ID, USERS.LOGIN, USERS.EMAIL, USERS.NAME, " +
            "USERS.BIRTHDAY FROM USERS WHERE USERS.ID IN(SELECT REQUESTED_USER_ID " +
            "FROM (SELECT REQUESTED_USER_ID FROM FRIENDS WHERE REQUESTING_USER_ID = %d AND STATUS IS TRUE) " +
            "WHERE REQUESTED_USER_ID INTERSECT (SELECT REQUESTED_USER_ID FROM FRIENDS " +
            "WHERE REQUESTING_USER_ID = %d AND STATUS IS TRUE))";
    private final JdbcTemplate jdbcTemplate;

    public FriendsDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long newFriendId, long targetUserId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        List<Long> friends = jdbcTemplate.query(String.format(GET_FRIENDS_IDS, targetUserId), this::mapRowToLong);
        if (friends.contains(newFriendId)) {
            return;
        }

        List<Long> subscribers = jdbcTemplate.query(String.format(
                GET_SUBSCRIBERS_IDS, targetUserId), this::mapRowToLong);
        if (subscribers.contains(newFriendId)) {
            List<Long> ids = jdbcTemplate.query(
                    String.format(SELECT_FALSE_FRIEND_ID, targetUserId, newFriendId), this::mapRowToLong);
            if (ids.size() == 1) {
                Long friends_id = ids.get(0);
                jdbcTemplate.update(String.format(UPDATE_STATUS_TRUE, friends_id), true);
            }
            return;
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(ADD_FRIEND, new String[]{"ID"});
            stmt.setLong(1, targetUserId);
            stmt.setLong(2, newFriendId);
            stmt.setBoolean(3, true);
            return stmt;
        }, keyHolder);
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(ADD_FRIEND, new String[]{"ID"});
            stmt.setLong(1, newFriendId);
            stmt.setLong(2, targetUserId);
            stmt.setBoolean(3, false);
            return stmt;
        }, keyHolder);
    }

    @Override
    public void deleteFriend(long friendUserId, long targetUserId) {
        jdbcTemplate.update(DELETE_FRIEND, targetUserId, friendUserId);
        jdbcTemplate.update(DELETE_FRIEND, friendUserId, targetUserId);
    }

    @Override
    public Set<User> findUserFriends(long targetUserId) {
        List<User> arrayFriends = jdbcTemplate.query(String.format(FIND_FRIENDS, targetUserId), this::mapRowToUser);
        return new HashSet<>(arrayFriends);
    }

    @Override
    public Set<User> findMutualFriends(long targetUser1Id, long targetUser2Id) {
        ArrayList<User> arrayFriends = (ArrayList<User>) jdbcTemplate.query(
                String.format(FIND_MUTUAL_FRIENDS, targetUser1Id, targetUser2Id), this::mapRowToUser);
        return new HashSet<>(arrayFriends);
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

        List<Long> subscribers = jdbcTemplate.query(String.format(
                GET_SUBSCRIBERS_IDS, user.getId()), this::mapRowToLong);
        Set<Long> subscribersSet = new HashSet<>(subscribers);
        user.setSubscribers(subscribersSet);

        return user;
    }

    private Long mapRowToLong(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("ID");
    }
}
