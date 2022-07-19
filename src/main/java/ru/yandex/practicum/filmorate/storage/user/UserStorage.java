package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user); //добавление пользователя

    User updateUser(User user); //модификация пользователя

    void deleteUser(long id); //удаление пользователя

    List<User> getAllUsers(); //поиск всех пользователей

    Optional<User> getUserById(long id); //получение пользователя по id
}
