package ru.yandex.practicum.filmorate.service.likes;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public interface LikesService {

    void addLike(long targetFilmId, long targetUserId); //добавление лайка

    void deleteLike(long targetFilmId, long targetUserId); //удаление лайка

    List<Film> findPopularFilms(int count); //вывод n наиболее популярных фильмов по количеству лайков
}
