package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public InMemoryUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    //добавление в друзья
    //пользователи становятся друзьями друг у друга без подтверждения (по ТЗ)
    @Override
    public void addFriend(long newFriendId, long targetUserId) {
        Map<Long, User> users = userStorage.getUsers();
        if (!(users.containsKey(newFriendId)) || !(users.containsKey(targetUserId))) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        if (newFriendId == targetUserId) {
            throw new IllegalArgumentException("Id не могут совпадать.");
        }
        User newFriend = users.get(newFriendId);
        User targetUser = users.get(targetUserId);
        if (targetUser.getFriends().contains(newFriend.getId())) {
            throw new ValidationException("Пользователи уже являются друзьями.");
        }
        newFriend.getFriends().add(targetUser.getId());
        targetUser.getFriends().add(newFriend.getId());
    }

    @Override
    //удаление из друзей
    public boolean deleteFriend(long friendUserId, long targetUserId) {
        Map<Long, User> users = userStorage.getUsers();
        if (!(users.containsKey(friendUserId)) || !(users.containsKey(targetUserId))) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        if (friendUserId == targetUserId) {
            throw new ValidationException("Пользователи должны быть разными.");
        }
        User friendUser = users.get(friendUserId);
        User targetUser = users.get(targetUserId);
        if (!(targetUser.getFriends().contains(friendUser.getId()))) {
            throw new ValidationException("Пользователи не являются друзьями.");
        }
        friendUser.getFriends().remove(targetUser.getId());
        targetUser.getFriends().remove(friendUser.getId());
        return true;
    }

    @Override
    //вывод списка друзей пользователя
    public Set<User> findUserFriends(long targetUserId) {
        Map<Long, User> users = userStorage.getUsers();
        if (!(users.containsKey(targetUserId))) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        Set<User> friends = new HashSet<>();
        User targetUser = users.get(targetUserId);
        for (Long id : targetUser.getFriends()) {
            friends.add(users.get(id));
        }
        return friends;
    }

    @Override
    //вывод списка общих друзей
    public Set<User> findMutualFriends(long targetUser1Id, long targetUser2Id) {
        Map<Long, User> users = userStorage.getUsers();
        if (!(users.containsKey(targetUser1Id)) || !(users.containsKey(targetUser2Id))) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        if (targetUser1Id == targetUser2Id) {
            throw new ValidationException("Пользователи должны быть разные");
        }
        Set<User> mutualFriends = new HashSet<>();
        User targetUser1 = users.get(targetUser1Id);
        User targetUser2 = users.get(targetUser2Id);
        for (long id1 : targetUser1.getFriends()) {
            for (long id2 : targetUser2.getFriends()) {
                if (id1 == id2) {
                    mutualFriends.add(users.get((id1)));
                }
            }
        }
        return mutualFriends;
    }
}
