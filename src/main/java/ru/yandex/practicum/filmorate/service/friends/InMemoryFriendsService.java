package ru.yandex.practicum.filmorate.service.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Component
public class InMemoryFriendsService implements FriendsService {
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFriendsService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(long newFriendId, long targetUserId) {
        User newFriend = userStorage.getAllUsers().get((int) newFriendId);
        User targetUser = userStorage.getAllUsers().get((int) targetUserId);
        newFriend.getFriends().add(targetUser.getId());
        targetUser.getFriends().add(newFriend.getId());
    }

    @Override
    public void deleteFriend(long friendUserId, long targetUserId) {
        User friendUser = userStorage.getAllUsers().get((int) friendUserId);
        User targetUser = userStorage.getAllUsers().get((int) targetUserId);
        friendUser.getFriends().remove(targetUser.getId());
        targetUser.getFriends().remove(friendUser.getId());
    }

    @Override
    public Set<User> findUserFriends(long targetUserId) {
        Set<User> friends = new HashSet<>();
        User targetUser = userStorage.getAllUsers().get((int) targetUserId);
        for (Long id : targetUser.getFriends()) {
            friends.add(userStorage.getAllUsers().get(Math.toIntExact(id)));
        }
        return friends;
    }

    @Override
    public Set<User> findMutualFriends(long targetUser1Id, long targetUser2Id) {
        Set<User> mutualFriends = new HashSet<>();
        User targetUser1 = userStorage.getAllUsers().get((int) targetUser1Id);
        User targetUser2 = userStorage.getAllUsers().get((int) targetUser2Id);
        for (long id1 : targetUser1.getFriends()) {
            for (long id2 : targetUser2.getFriends()) {
                if (id1 == id2) {
                    mutualFriends.add(userStorage.getAllUsers().get(Math.toIntExact(id1)));
                }
            }
        }
        return mutualFriends;
    }
}
