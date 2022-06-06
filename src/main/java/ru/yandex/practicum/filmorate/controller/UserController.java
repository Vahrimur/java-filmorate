package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private long id = 0;
    private final Map<Long, User> users = new HashMap<>(); //<id, user>

    @PostMapping //создание пользователя
    public User create(@RequestBody User user) {
        try {
            User.validateUser(user);
            if (users.containsValue(user)) {
                throw new ValidationException("Такой пользователь уже зарегистрирован.");
            }
            if (users.containsKey(user.getId())) {
                throw new ValidationException("Пользователь с таким id уже существует.");
            }
            user.setId(makeId());
            users.put(user.getId(), user);
            log.debug("Пользователь добавлен: {}", user);
            return user;
        } catch (ValidationException e) {
            log.debug("Возникла ошибка: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping //обновление пользователя
    public User update(@RequestBody User user) {
        try {
            User.validateUser(user);
            if (!users.containsKey(user.getId())) {
                throw new ValidationException("Пользователя с таким id не существует.");
            }
            if (users.containsValue(user)) {
                throw new ValidationException("В переданной информации нет данных для обновления.");
            }
            users.put(user.getId(), user);
            log.debug("Пользователь обновлён: {}", user);
            return user;
        } catch (ValidationException e) {
            log.debug("Возникла ошибка: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping //получение списка всех пользователей
    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    public long makeId() {
        return ++id;
    }
}
