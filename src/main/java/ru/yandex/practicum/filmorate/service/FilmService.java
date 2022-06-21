package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    //добавление лайка
    //каждый пользователь может поставить лайк фильму только один раз (по ТЗ)
    public void addLike(long targetFilmId, long targetUserId) {
        Map<Long, User> users = userStorage.getUsers();
        Map<Long, Film> films = filmStorage.getFilms();
        if (!(films.containsKey(targetFilmId)) || !(users.containsKey(targetUserId))) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        User targetUser = users.get(targetUserId);
        Film targetFilm = films.get(targetFilmId);
        if (targetFilm.getUserLikes().contains(targetUser.getId())) {
            throw new ValidationException("Этот пользователь уже ставил лайк.");
        }
        targetFilm.getUserLikes().add(targetUser.getId());
    }

    //удаление лайка
    public void deleteLike(long targetFilmId, long targetUserId) {
        Map<Long, User> users = userStorage.getUsers();
        Map<Long, Film> films = filmStorage.getFilms();
        if (!(films.containsKey(targetFilmId)) || !(users.containsKey(targetUserId))) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        User targetUser = users.get(targetUserId);
        Film targetFilm = films.get(targetFilmId);
        if (!(targetFilm.getUserLikes().contains(targetUser.getId()))) {
            throw new ValidationException("Этот пользователь не ставил лайк.");
        }
        targetFilm.getUserLikes().remove(targetUser.getId());
    }

    //вывод n наиболее популярных фильмов по количеству лайков
    public ArrayList<Film> findPopularFilms(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count не может быть отрицательным.");
        }
        Comparator<Film> comparator = (f1, f2) -> {
            int f1Likes = f1.getUserLikes().size();
            int f2Likes = f2.getUserLikes().size();
            return f1Likes - f2Likes;
        };

        Map<Long, Film> films = filmStorage.getFilms();
        ArrayList<Film> popularFilms = new ArrayList<>(films.values());
        popularFilms.sort(comparator.reversed());
        if (films.size() < count) {
            return popularFilms;
        } else {
            return (ArrayList<Film>) popularFilms.stream().limit(count).collect(Collectors.toList());
        }
    }
}
