package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreStorage genreStorage;

    public GenreController(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping() //получение всех жанров
    public Collection<Genre> findAll() {
        Collection<Genre> genres = genreStorage.findAllGenres();
        log.debug("Текущее количество жанров: {}", genres.size());
        return genres;
    }

    @GetMapping("/{genreId}") //получение жанра по id
    public Genre findById(@PathVariable Integer genreId) {
        if (!(genreStorage.getGenres().containsKey(genreId))) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        Genre genre = genreStorage.getGenres().get(genreId);
        log.debug("Получен жанр: {}", genre);
        return genre;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final RuntimeException e) {
        log.warn("Возникла ошибка данных: {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }
}
