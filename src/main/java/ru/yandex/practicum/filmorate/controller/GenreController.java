package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping() //получение всех жанров
    public List<Genre> findAll() {
        List<Genre> genres = genreService.getAllGenres();
        log.debug("Текущее количество жанров: {}", genres.size());
        return genres;
    }

    @GetMapping("/{genreId}") //получение жанра по id
    public Genre findById(@PathVariable Integer genreId) {
        Genre genre = genreService.getGenreById(genreId);
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
