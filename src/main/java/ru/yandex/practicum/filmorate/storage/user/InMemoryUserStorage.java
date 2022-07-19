package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long id = 0;
    private final Map<Long, User> users = new HashMap<>(); //<id, user>

    private long makeId() {
        return ++id;
    }

    @Override
    public User addUser(User user) {
        user.setId(makeId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователя с таким id не существует.");
        }
        if (users.containsValue(user)) {
            throw new ValidationException("В переданной информации нет данных для обновления.");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        if (!(users.containsKey(id))) {
            throw new ValidationException("Такого пользователя не существует.");
        }
        users.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.of(users.get(id));
    }
}
