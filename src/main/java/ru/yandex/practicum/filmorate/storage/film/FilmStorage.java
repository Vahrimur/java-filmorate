package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film); //добавление фильма

    Film updateFilm(Film film); //модификация фильма

    void deleteFilm(long id); //удаление фильма

    List<Film> getAllFilms(); //поиск всех фильмов

    Optional<Film> getFilmById(long id);
}
