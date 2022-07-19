package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> getAllGenres(); //поиск всех жанров

    Optional<Genre> getGenreById(int id); //поиск жанра по id
}
