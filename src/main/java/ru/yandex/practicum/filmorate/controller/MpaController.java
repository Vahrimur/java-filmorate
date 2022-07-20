package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {
    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping() //получение всех рейтингов
    public List<Mpa> findAll() {
        List<Mpa> mpa_rates = mpaService.getAllMpas();
        log.debug("Текущее количество рейтингов: {}", mpa_rates.size());
        return mpa_rates;
    }

    @GetMapping("/{mpaId}") //получение рейтинга по id
    public Mpa findById(@PathVariable Integer mpaId) {
        Mpa mpa = mpaService.getMpaById(mpaId);
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
