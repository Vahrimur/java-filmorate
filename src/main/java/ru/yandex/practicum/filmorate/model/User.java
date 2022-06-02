package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private long id; //идентификатор
    private String email; //электронная почта, в будущем при изменении email нужно заменить элемент юзера в мапе
    private String login; // логин пользователя
    private String name; // имя для отображения
    private LocalDate birthday;

    public static void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ") ||user.getLogin().isBlank()) {
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
    }
}
