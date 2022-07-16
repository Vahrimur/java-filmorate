package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Map;

public interface MpaStorage {
    Collection<Mpa> findAllMpas(); //поиск всех рейтингов mpa

    Map<Integer, Mpa> getMpas();
}
