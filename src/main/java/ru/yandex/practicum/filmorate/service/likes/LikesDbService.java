package ru.yandex.practicum.filmorate.service.likes;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
public class LikesDbService implements LikesService {
    private static final String ADD_LIKE = "INSERT INTO LIKES (FILM_ID, USER_ID) " +
            "VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ? ";
    private static final String GET_TOP_FILMS = "SELECT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE, " +
            "FILMS.DURATION, FILMS.RATING_ID, RATINGS.NAME, GENRES.ID, GENRES.NAME, COUNT(LIKES.FILM_ID) " +
            "FROM FILMS JOIN RATINGS ON FILMS.RATING_ID = RATINGS.ID LEFT JOIN FILM_GENRES ON " +
            "FILMS.ID = FILM_GENRES.FILM_ID LEFT JOIN GENRES on FILM_GENRES.GENRE_ID = GENRES.ID " +
            "LEFT JOIN LIKES on FILMS.ID = LIKES.FILM_ID GROUP BY FILMS.ID " +
            "ORDER BY COUNT(LIKES.FILM_ID) DESC LIMIT %d";
    private static final String GET_GENRES = "SELECT FILM_GENRES.GENRE_ID, GENRES.NAME FROM FILM_GENRES " +
            "LEFT JOIN GENRES on FILM_GENRES.GENRE_ID = GENRES.ID WHERE FILM_GENRES.FILM_ID = %d";
    private static final String GET_LIKES = "SELECT USER_ID AS ID FROM LIKES WHERE FILM_ID = %d";
    private final JdbcTemplate jdbcTemplate;

    public LikesDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long targetFilmId, long targetUserId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(ADD_LIKE, new String[]{"ID"});
            stmt.setLong(1, targetFilmId);
            stmt.setLong(2, targetUserId);
            return stmt;
        }, keyHolder);
    }

    @Override
    public void deleteLike(long targetFilmId, long targetUserId) {
        jdbcTemplate.update(DELETE_LIKE, targetFilmId, targetUserId);
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count не может быть отрицательным.");
        }
        return jdbcTemplate.query(String.format(GET_TOP_FILMS, count), this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film(resultSet.getLong("FILMS.ID"),
                resultSet.getString("FILMS.NAME"),
                resultSet.getString("FILMS.DESCRIPTION"),
                resultSet.getDate("FILMS.RELEASE_DATE").toLocalDate(),
                resultSet.getInt("FILMS.DURATION"),
                new Mpa(resultSet.getInt("FILMS.RATING_ID"), resultSet.getString("RATINGS.NAME")), null, null
        );

        ArrayList<Genre> newGenres = (ArrayList<Genre>) jdbcTemplate.query(
                String.format(GET_GENRES, film.getId()), this::mapRowToGenre);
        Set<Genre> set = new HashSet<>(newGenres);
        newGenres.clear();
        newGenres.addAll(set);
        film.setGenres(Optional.of(newGenres));

        ArrayList<Long> likes = (ArrayList<Long>) jdbcTemplate.query(
                String.format(GET_LIKES, film.getId()), this::mapRowToLong);
        Set<Long> likesSet = new HashSet<>(likes);
        film.setUserLikes(likesSet);

        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("FILM_GENRES.GENRE_ID"), resultSet.getString("GENRES.NAME"));
    }

    private Long mapRowToLong(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("ID");
    }
}
