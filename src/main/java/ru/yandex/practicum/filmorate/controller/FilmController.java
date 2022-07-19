package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;


    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping() //добавление фильма
    public Film create(@RequestBody Film film) {
        Film addedFilm = filmService.addFilm(film);
        log.debug("Фильм добавлен: {}", addedFilm);
        return addedFilm;
    }

    @PutMapping //обновление фильма
    public Film update(@RequestBody Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        log.debug("Фильм обновлён: {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}") //пользователь ставит лайк фильму
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
        log.debug("Пользователь с id {} поставил лайк фильму с id {}", userId, id);
    }

    @GetMapping() //получение всех фильмов
    public Collection<Film> findAll() {
        Collection<Film> films = filmService.getAllFilms();
        log.debug("Текущее количество фильмов: {}", films.size());
        return films; //change type of data
    }

    @GetMapping("/{filmId}") //получение фильма по id
    public Film findById(@PathVariable long filmId) {
        Film film = filmService.getFilmById(filmId);
        log.debug("Получен фильм: {}", film);
        return film;
    }

    @GetMapping("/popular") // возвращает список из count топ-фильмов
    public List<Film> findPopularFilms(@RequestParam Optional<Integer> count) {
        if (count.isPresent()) {
            List<Film> films = filmService.findPopularFilms(count.get());
            log.debug("Получен топ-{} фильмов", films.size());
            return films;
        } else {
            List<Film> films = filmService.findPopularFilms(10);
            log.debug("Получен топ-{} фильмов", films.size());
            return films; //если count не задано, возвращает первые 10
        }
    }

    @DeleteMapping("/{id}/like/{userId}") //пользователь удаляет лайк
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        if (id < 0 || userId < 0) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        filmService.deleteLike(id, userId);
        log.debug("Пользователь с id {} успешно удалил лайк фильму с id {}", userId, id);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle1(final RuntimeException e) {
        log.warn("Возникла ошибка валидации: {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка валидации", e.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle2(final RuntimeException e) {
        log.warn("Возникла ошибка данных: {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle3(final RuntimeException e) {
        log.warn("Возникла ошибка: {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка", e.getMessage()
        );
    }
}
