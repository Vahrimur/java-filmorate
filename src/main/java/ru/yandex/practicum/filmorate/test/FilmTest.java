package ru.yandex.practicum.filmorate.test;

import org.junit.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class FilmTest<T extends Film> {
    @Test
    public void shouldValidate() {
        Film film = new Film(1, "", "description",
                LocalDate.of(2002, 1, 1), 120);
        ValidationException ex1 = assertThrows(
                ValidationException.class,
                () -> Film.validateFilm(film)
        );
        assertEquals("Название не может быть пустым.", ex1.getMessage());

        film.setName("name");
        film.setDescription("toomanywords-toomanywords-toomanywords-toomanywords-" +
                "toomanywords-toomanywords-toomanywords-toomanywords-toomanywords-toomanywords-toomanywords-" +
                "toomanywords-toomanywords-toomanywords-toomanywords-toomanywords-toomanywords-toomanywords");
        ValidationException ex2 = assertThrows(
                ValidationException.class,
                () -> Film.validateFilm(film)
        );
        assertEquals("Максимальная длина описания — 200 символов.", ex2.getMessage());

        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        ValidationException ex3 = assertThrows(
                ValidationException.class,
                () -> Film.validateFilm(film)
        );
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года.", ex3.getMessage());

        film.setReleaseDate(LocalDate.of(2002, 1, 1));
        film.setDuration(-100);
        ValidationException ex4 = assertThrows(
                ValidationException.class,
                () -> Film.validateFilm(film)
        );
        assertEquals("Продолжительность фильма должна быть положительной.", ex4.getMessage());
    }
}
