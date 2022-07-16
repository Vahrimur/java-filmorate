package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;

public interface GenreStorage {
    Collection<Genre> findAllGenres(); //поиск всех жанров

    Map<Integer, Genre> getGenres();
}
