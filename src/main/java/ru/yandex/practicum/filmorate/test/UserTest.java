package ru.yandex.practicum.filmorate.test;

import org.junit.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void shouldValidate() {
        User user = new User(1, "email@yandex.ru", "", "name",
                LocalDate.of(1991, 6, 22));
        ValidationException ex1 = assertThrows(
                ValidationException.class,
                () -> User.validateUser(user)
        );
        assertEquals("Логин не может быть пустым и содержать пробелы.", ex1.getMessage());

        user.setLogin(null);
        ValidationException ex2 = assertThrows(
                ValidationException.class,
                () -> User.validateUser(user)
        );
        assertEquals("Логин не может быть пустым и содержать пробелы.", ex2.getMessage());

        user.setLogin("login");
        user.setEmail("wrongEmail");
        ValidationException ex3 = assertThrows(
                ValidationException.class,
                () -> User.validateUser(user)
        );
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", ex3.getMessage());

        user.setEmail("email@yandex.ru");
        user.setName(null);
        User.validateUser(user);
        assertNotNull(user.getName());
        assertEquals(user.getName(), user.getLogin());

        user.setBirthday(LocalDate.of(2025, 6, 22));
        ValidationException ex4 = assertThrows(
                ValidationException.class,
                () -> User.validateUser(user)
        );
        assertEquals("Дата рождения не может быть в будущем.", ex4.getMessage());
    }
}
