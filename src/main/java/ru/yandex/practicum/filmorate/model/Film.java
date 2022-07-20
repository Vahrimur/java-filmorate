package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private long id; //идентификатор
    private String name; //название
    private String description; //описание
    private LocalDate releaseDate; //дата релиза
    private int duration; //продолжительность фильма
    private Mpa mpa; // id рейтинга фильма (из таблицы рейтингов)
    private Set<Long> userLikes; // список с неповторяющимися (по ТЗ) лайками пользователей
    private Optional<ArrayList<Genre>> genres; // список жанров (из таблицы жанров, может быть несколько)

    public static void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (film.getUserLikes() == null) {
            film.setUserLikes(new HashSet<>());
        }
        if (film.getGenres().isEmpty()) {
            film.setGenres(Optional.of(new ArrayList<>()));
        }
    }
}
