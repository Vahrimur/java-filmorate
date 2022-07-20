package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.friends.FriendsService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendsService friendsService;

    @Autowired
    public UserService(UserStorage userStorage, FriendsService friendsService) {
        this.userStorage = userStorage;
        this.friendsService = friendsService;
    }

    public User addUser(User user) {
        User.validateUser(user);
        return userStorage.addUser(user);
    }


    public User updateUser(User user) {
        if (user.getId() < 0) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        User.validateUser(user);
        return userStorage.updateUser(user);
    }

    public void deleteUser(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        userStorage.deleteUser(id);
    }

    public List<User> getAllUsers() { //поиск всех пользователей
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id).orElseThrow(() -> new IllegalArgumentException(
                "Пользователя с таким id не существует"));
    }

    public void addFriend(long newFriendId, long targetUserId) {
        if (newFriendId == targetUserId) {
            throw new IllegalArgumentException("Id не могут совпадать.");
        }
        validateUser(newFriendId);
        validateUser(targetUserId);
        friendsService.addFriend(newFriendId, targetUserId);
    }

    public void deleteFriend(long friendUserId, long targetUserId) {
        if (friendUserId == targetUserId) {
            throw new ValidationException("Пользователи должны быть разными.");
        }
        validateUser(friendUserId);
        validateUser(targetUserId);
        friendsService.deleteFriend(friendUserId, targetUserId);
    }

    public Set<User> findUserFriends(long targetUserId) {
        validateUser(targetUserId);
        return friendsService.findUserFriends(targetUserId);
    }

    public Set<User> findMutualFriends(long targetUser1Id, long targetUser2Id) {
        validateUser(targetUser1Id);
        validateUser(targetUser2Id);
        if (targetUser1Id == targetUser2Id) {
            throw new ValidationException("Пользователи должны быть разные");
        }
        return friendsService.findMutualFriends(targetUser1Id, targetUser2Id);
    }

    private void validateUser(long userId) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new IllegalArgumentException("Введён некорректный id пользователя."));
    }
}
