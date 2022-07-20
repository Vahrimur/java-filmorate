package ru.yandex.practicum.filmorate.service.friends;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

@Service
public interface FriendsService {

    void addFriend(long newFriendId, long targetUserId); //добавление друга

    void deleteFriend(long friendUserId, long targetUserId); //удаление из друзей

    Set<User> findUserFriends(long targetUserId); //вывод списка друзей пользователя

    Set<User> findMutualFriends(long targetUser1Id, long targetUser2Id); //вывод списка общих друзей
}
