package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface UserService {
    void addFriend(long newFriendId, long targetUserId);

    boolean deleteFriend(long friendUserId, long targetUserId);

    Set<User> findUserFriends(long targetUserId);

    Set<User> findMutualFriends(long targetUser1Id, long targetUser2Id);
}
