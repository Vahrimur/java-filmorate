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
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @DirtiesContext
    @Test
    public void shouldAddAndGetUser() {
        User user = new User(0, "email@yandex.ru", "login", "name",
                LocalDate.of(1991, 6, 22), null, null);
        userStorage.addUser(user);
        userStorage.addUser(user);
        List<User> users = userStorage.getAllUsers();
        assertThat(users.size()).isEqualTo(2);
        User userDb1 = users.get(0);
        User userDb2 = users.get(1);
        assertThat(user.getId()).isNotEqualTo(userDb1.getId());
        assertThat(userDb2.getId()).isEqualTo(2);
    }

    @DirtiesContext
    @Test
    public void shouldDeleteUser() {
        User user = new User(0, "email@yandex.ru", "login", "name",
                LocalDate.of(1991, 6, 22), null, null);
        userStorage.addUser(user);
        List<User> users1 = userStorage.getAllUsers();
        assertThat(users1.size()).isEqualTo(1);
        userStorage.deleteUser(1);
        List<User> users2 = userStorage.getAllUsers();
        assertThat(users2.size()).isEqualTo(0);
    }

    @DirtiesContext
    @Test
    public void shouldUpdateUser() {
        User user = new User(0, "email@yandex.ru", "login", "name",
                LocalDate.of(1991, 6, 22), null, null);
        userStorage.addUser(user);
        List<User> users1 = userStorage.getAllUsers();
        User userDb1 = users1.get(0);
        userDb1.setName("new name");
        userDb1.setEmail("email@mail.ru");
        userStorage.updateUser(userDb1);
        List<User> users2 = userStorage.getAllUsers();
        User userDb2 = users2.get(0);
        assertThat(userDb2.getName()).isEqualTo("new name");
        assertThat(userDb2.getEmail()).isEqualTo("email@mail.ru");
    }

    @DirtiesContext
    @Test
    public void shouldFindAllUsers() {
        User user = new User(0, "email@yandex.ru", "login", "name",
                LocalDate.of(1991, 6, 22), null, null);
        userStorage.addUser(user);
        userStorage.addUser(user);
        userStorage.addUser(user);
        Collection<User> users = userStorage.getAllUsers();
        assertThat(users.size()).isEqualTo(3);
        List<User> usersList = new ArrayList<>(users);
        User userDb = usersList.get(2);
        assertThat(userDb.getName()).isEqualTo("name");
        assertThat(userDb.getEmail()).isEqualTo("email@yandex.ru");
        assertThat(userDb.getId()).isEqualTo(3);

    }
}
