package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
    private final MpaStorage mpaStorage;

    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping() //получение всех рейтингов
    public Collection<Mpa> findAll() {
        Collection<Mpa> mpa_rates = mpaStorage.findAllMpas();
        log.debug("Текущее количество рейтингов: {}", mpa_rates.size());
        return mpa_rates;
    }

    @GetMapping("/{mpaId}") //получение рейтинга по id
    public Mpa findById(@PathVariable Integer mpaId) {
        if (!(mpaStorage.getMpas().containsKey(mpaId))) {
            throw new IllegalArgumentException("Введён некорректный id.");
        }
        Mpa mpa = mpaStorage.getMpas().get(mpaId);
        log.debug("Получен рейтинг mpa: {}", mpa);
        return mpa;
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
