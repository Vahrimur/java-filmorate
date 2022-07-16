package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

public interface FilmService {
    void addLike(long targetFilmId, long targetUserId);

    boolean deleteLike(long targetFilmId, long targetUserId);

    ArrayList<Film> findPopularFilms(int count);
}