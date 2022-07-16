package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Film addFilm(Film film); //добавление фильма

    boolean deleteFilm(Film film); //удаление фильма

    Film updateFilm(Film film); //модификация фильма

    Collection<Film> findAllFilms(); //поиск всех фильмов

    Map<Long, Film> getFilms();
}
