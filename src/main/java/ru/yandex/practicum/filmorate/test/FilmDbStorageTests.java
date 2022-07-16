package ru.yandex.practicum.filmorate.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    private final FilmDbStorage filmStorage;

    @DirtiesContext
    @Test
    public void shouldAddAndGetFilm() {
        Genre g = new Genre(1, "Комедия");
        ArrayList<Genre> genre = new ArrayList<>();
        genre.add(g);
        Film film = new Film(1, "", "description",
                LocalDate.of(2002, 1, 1), 120, new Mpa(1, "G"), null, Optional.of(genre));
        filmStorage.addFilm(film);
        filmStorage.addFilm(film);
        Map<Long, Film> films = filmStorage.getFilms();
        assertThat(films.size()).isEqualTo(2);
        Film filmDb1 = films.get(1L);
        Film filmDb2 = films.get(2L);
        assertThat(film.getId()).isNotEqualTo(filmDb1.getId());
        assertThat(filmDb2.getId()).isEqualTo(2);
        assertThat(filmDb1.getDescription()).isEqualTo("description");
    }

    @DirtiesContext
    @Test
    public void shouldDeleteFilm() {
        Genre g = new Genre(1, "Комедия");
        ArrayList<Genre> genre = new ArrayList<>();
        genre.add(g);
        Film film = new Film(1, "", "description",
                LocalDate.of(2002, 1, 1), 120, new Mpa(1, "G"), null, Optional.of(genre));
        filmStorage.addFilm(film);
        Map<Long, Film> films1 = filmStorage.getFilms();
        assertThat(films1.size()).isEqualTo(1);
        Film filmDb1 = films1.get(1L);
        filmStorage.deleteFilm(filmDb1);
        Map<Long, Film> films2 = filmStorage.getFilms();
        assertThat(films2.size()).isEqualTo(0);
    }

}
