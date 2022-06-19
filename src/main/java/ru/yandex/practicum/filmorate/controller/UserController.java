package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping //создание пользователя
    public User create(@RequestBody User user) {
        User.validateUser(user);
        User addedUser = userService.getUserStorage().addUser(user);
        log.debug("Пользователь добавлен: {}", addedUser);
        return addedUser;
    }

    @PutMapping //обновление пользователя
    public User update(@RequestBody User user) {
        if (user.getId() < 0) {
            throw new RuntimeException("Введён некорректный id.");
        }
        User.validateUser(user);
        User updatedUser = userService.getUserStorage().updateUser(user);
        log.debug("Пользователь обновлён: {}", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}") //добавление в друзья
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(friendId, id);
        log.debug("Пользователь с id {} добавлен в друзья пользователю с id {}", friendId, id);
    }

    @GetMapping //получение списка всех пользователей
    public Collection<User> findAll() {
        Collection<User> users = userService.getUserStorage().findAllUsers();
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    @GetMapping("/{userId}") //получение пользователя по id
    public User findById(@PathVariable long userId) {
        if (!(userService.getUserStorage().getUsers().containsKey(userId))) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        User user = userService.getUserStorage().getUsers().get(userId);
        log.debug("Получен пользователь: {}", user);
        return user;
    }

    @GetMapping("/{id}/friends") //возвращает список пользователей, являющихся его друзьями
    public Set<User> findUserFriends(@PathVariable long id) {
        Set<User> friends = userService.findUserFriends(id);
        log.debug("Количество друзей пользователя с id {}: {}", id, friends.size());
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}") //список друзей, общих с другим пользователем
    public Set<User> findMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        Set<User> mutualFriends = userService.findMutualFriends(id, otherId);
        log.debug("Количество общих друзей пользователей с id {} и {}: {}", id, otherId,
                mutualFriends.size());
        return mutualFriends;
    }

    @DeleteMapping("/{id}/friends/{friendId}") //удаление из друзей
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(friendId, id);
        log.debug("Пользователь с id {} успешно удалён из друзей пользователя с id {}", friendId, id);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle1(final RuntimeException e) {
        log.warn("Возникла ошибка валидации: {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка валидации", e.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle2(final RuntimeException e) {
        log.warn("Возникла ошибка данных: {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle3(final RuntimeException e) {
        log.warn("Возникла ошибка: {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка", e.getMessage()
        );
    }
}
