package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpas() {
        return mpaStorage.getAllMpas();
    }

    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id).orElseThrow(() -> new IllegalArgumentException(
                "Рейтинга с таким id не существует"));
    }
}
