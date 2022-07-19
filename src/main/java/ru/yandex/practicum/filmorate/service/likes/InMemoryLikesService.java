package ru.yandex.practicum.filmorate.service.likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryLikesService implements LikesService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryLikesService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void addLike(long targetFilmId, long targetUserId) {
        User targetUser = userStorage.getAllUsers().get((int) targetUserId);
        Film targetFilm = filmStorage.getAllFilms().get((int) targetFilmId);
        targetFilm.getUserLikes().add(targetUser.getId());
    }

    @Override
    public void deleteLike(long targetFilmId, long targetUserId) {
        User targetUser = userStorage.getAllUsers().get((int) targetUserId);
        Film targetFilm = filmStorage.getAllFilms().get((int) targetFilmId);
        targetFilm.getUserLikes().remove(targetUser.getId());
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        Comparator<Film> comparator = (f1, f2) -> {
            int f1Likes = f1.getUserLikes().size();
            int f2Likes = f2.getUserLikes().size();
            return f1Likes - f2Likes;
        };

        List<Film> popularFilms = filmStorage.getAllFilms();
        popularFilms.sort(comparator.reversed());
        if (popularFilms.size() < count) {
            return popularFilms;
        } else {
            return popularFilms.stream().limit(count).collect(Collectors.toList());
        }
    }
}
