package ru.yandex.practicum.filmorate.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @DirtiesContext
    @Test
    public void shouldAddAndGetUser() { //добавление пользователя и получение всех пользователей
        User user = new User(0, "email@yandex.ru", "login", "name",
                LocalDate.of(1991, 6, 22), null, null);
        userStorage.addUser(user);
        userStorage.addUser(user);
        Map<Long, User> users = userStorage.getUsers();
        assertThat(users.size()).isEqualTo(2);
        User userDb1 = users.get(1L);
        User userDb2 = users.get(2L);
        assertThat(user.getId()).isNotEqualTo(userDb1.getId());
        assertThat(userDb2.getId()).isEqualTo(2);
    }

    @DirtiesContext
    @Test
    public void shouldDeleteUser() { //удаление пользователя
        User user = new User(0, "email@yandex.ru", "login", "name",
                LocalDate.of(1991, 6, 22), null, null);
        userStorage.addUser(user);
        Map<Long, User> users1 = userStorage.getUsers();
        assertThat(users1.size()).isEqualTo(1);
        User userDb1 = users1.get(1L);
        userStorage.deleteUser(userDb1);
        Map<Long, User> users2 = userStorage.getUsers();
        assertThat(users2.size()).isEqualTo(0);
    }

    @DirtiesContext
    @Test
    public void shouldUpdateUser() { //обноваление пользователя
        User user = new User(0, "email@yandex.ru", "login", "name",
                LocalDate.of(1991, 6, 22), null, null);
        userStorage.addUser(user);
        Map<Long, User> users1 = userStorage.getUsers();
        User userDb1 = users1.get(1L);
        userDb1.setName("new name");
        userDb1.setEmail("email@mail.ru");
        userStorage.updateUser(userDb1);
        Map<Long, User> users2 = userStorage.getUsers();
        User userDb2 = users2.get(1L);
        assertThat(userDb2.getName()).isEqualTo("new name");
        assertThat(userDb2.getEmail()).isEqualTo("email@mail.ru");
    }

    @DirtiesContext
    @Test
    public void shouldFindAllUsers() { //поиск всех пользователей
        User user = new User(0, "email@yandex.ru", "login", "name",
                LocalDate.of(1991, 6, 22), null, null);
        userStorage.addUser(user);
        userStorage.addUser(user);
        userStorage.addUser(user);
        Collection<User> users = userStorage.findAllUsers();
        assertThat(users.size()).isEqualTo(3);
        List<User> usersList = new ArrayList<>(users);
        User userDb = usersList.get(2);
        assertThat(userDb.getName()).isEqualTo("name");
        assertThat(userDb.getEmail()).isEqualTo("email@yandex.ru");
        assertThat(userDb.getId()).isEqualTo(3);

    }
}
