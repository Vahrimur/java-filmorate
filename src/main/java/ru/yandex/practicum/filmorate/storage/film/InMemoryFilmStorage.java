package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 0;
    private final Map<Long, Film> films = new HashMap<>();

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
    public void deleteFilm(long id) {
        if (!(films.containsKey(id))) {
            throw new ValidationException("Такого фильма нет в коллекции."); //пернести валидацию в сервис
        }
        films.remove(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.of(films.get(id));
    }
}
