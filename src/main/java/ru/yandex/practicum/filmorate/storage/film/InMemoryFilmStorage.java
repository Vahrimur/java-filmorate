package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 0;
    private final Map<Long, Film> films = new HashMap<>(); //<id, film>

    private long makeId() {
        return ++id;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(makeId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean deleteFilm(Film film) {
        if (!(films.containsValue(film))) {
            throw new ValidationException("Такого фильма нет в коллекции.");
        }
        films.remove(film.getId());
        return true;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильма с таким id нет в коллекции.");
        }
        if (films.containsValue(film)) {
            throw new ValidationException("В переданной информации нет данных для обновления.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }
}
