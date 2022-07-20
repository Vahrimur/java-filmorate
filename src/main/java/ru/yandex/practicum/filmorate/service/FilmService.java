package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.likes.LikesService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesService likesService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikesService likesService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesService = likesService;
    }

    public Film addFilm(Film film) {
        Film.validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() < 0) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        Film.validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        filmStorage.deleteFilm(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id).orElseThrow(() -> new IllegalArgumentException(
                "Фильма с таким id не существует"));
    }

    public void addLike(long targetFilmId, long targetUserId) {
        validateFilmAndUser(targetFilmId, targetUserId);
        likesService.addLike(targetFilmId, targetUserId);
    }

    public void deleteLike(long targetFilmId, long targetUserId) {
        validateFilmAndUser(targetFilmId, targetUserId);
        likesService.deleteLike(targetFilmId, targetUserId);
    }

    public List<Film> findPopularFilms(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count не может быть отрицательным.");
        }
        return likesService.findPopularFilms(count);
    }

    private void validateFilmAndUser(long filmId, long userId) {
        filmStorage.getFilmById(filmId).orElseThrow(() ->
                new IllegalArgumentException("Введён некорректный id фильма."));
        userStorage.getUserById(userId).orElseThrow(() ->
                new IllegalArgumentException("Введён некорректный id пользователя."));
    }
}
