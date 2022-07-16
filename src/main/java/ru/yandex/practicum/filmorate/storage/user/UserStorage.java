package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    User addUser(User user); //добавление пользователя

    boolean deleteUser(User user); //удаление пользователя

    User updateUser(User user); //модификация пользователя

    Collection<User> findAllUsers(); //поиск всех пользователей

    Map<Long, User> getUsers();
}
