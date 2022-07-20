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
import java.util.List;
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
        Genre genre = new Genre(1, "Комедия");
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(genre);
        Film film = new Film(1, "", "description",
                LocalDate.of(2002, 1, 1), 120, new Mpa(1, "G"), null, Optional.of(genres));
        filmStorage.addFilm(film);
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        assertThat(films.size()).isEqualTo(2);
        Film filmDb1 = films.get(0);
        Film filmDb2 = films.get(1);
        assertThat(film.getId()).isNotEqualTo(filmDb1.getId());
        assertThat(filmDb2.getId()).isEqualTo(2);
        assertThat(filmDb1.getDescription()).isEqualTo("description");
    }

    @DirtiesContext
    @Test
    public void shouldDeleteFilm() {
        Genre genre = new Genre(1, "Комедия");
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(genre);
        Film film = new Film(1, "", "description",
                LocalDate.of(2002, 1, 1), 120, new Mpa(1, "G"), null, Optional.of(genres));
        filmStorage.addFilm(film);
        List<Film> films1 = filmStorage.getAllFilms();
        assertThat(films1.size()).isEqualTo(1);
        Film filmDb1 = films1.get(0);
        filmStorage.deleteFilm(1);
        List<Film> films2 = filmStorage.getAllFilms();
        assertThat(films2.size()).isEqualTo(0);
    }
}
