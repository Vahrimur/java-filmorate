package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private long id = 0;
    private final Map<Long, Film> films = new HashMap<>(); //<id, film>

    @PostMapping() //добавление фильма
    public Film create(@RequestBody Film film) {
        try {
            Film.validateFilm(film);
            if (films.containsValue(film)) {
                throw new ValidationException("Данный фильм уже есть в коллекции.");
            }
            if (films.containsKey(film.getId())) {
                throw new ValidationException("Фильм с таким id уже существует.");
            }
            film.setId(makeId());
            films.put(film.getId(), film);
            log.debug("Фильм добавлен: {}", film);
            return film;
        } catch(ValidationException e) {
            log.debug("Возникла ошибка: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping //обновление фильма
    public Film update(@RequestBody Film film) {
        try {
            Film.validateFilm(film);
            if (!films.containsKey(film.getId())) {
                throw new ValidationException("Такого фильма нет в коллекции.");
            }
            if (films.containsValue(film)) {
                throw new ValidationException("В переданной информации нет данных для обновления.");
            }
            films.put(film.getId(), film);
            log.debug("Фильм обновлён: {}", film);
            return film;
        } catch(ValidationException e) {
            log.debug("Возникла ошибка: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping() //получение всех фильмов
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    public long makeId() {
        return ++id;
    }
}
