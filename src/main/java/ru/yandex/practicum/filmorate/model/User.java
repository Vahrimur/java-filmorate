package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private long id; //идентификатор
    private String email; //электронная почта, в будущем при изменении email нужно заменить элемент юзера в мапе
    private String login; // логин пользователя
    private String name; // имя для отображения
    private LocalDate birthday; //день рождения
    private Set<Long> friends; // список с неповторяющимися (по ТЗ) id друзей пользователя (из таблицы, только подтвержденные друзья)
    private Set<Long> subscribers; // список с неповторяющимися (по ТЗ) id предложивших пользователю дружбу (из таблицы, только неподтвержденные друзья)


    public static void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (user.getSubscribers() == null) {
            user.setSubscribers(new HashSet<>());
        }
    }
}
