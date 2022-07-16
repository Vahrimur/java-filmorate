package ru.yandex.practicum.filmorate.service;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component("filmrDbService")
@Primary
public class FilmDbService implements FilmService {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long targetFilmId, long targetUserId) {
        String sqlQuery = "INSERT INTO LIKES (FILM_ID, USER_ID) " +
                "VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            stmt.setLong(1, targetFilmId);
            stmt.setLong(2, targetUserId);
            return stmt;
        }, keyHolder);
    }

    @Override
    public boolean deleteLike(long targetFilmId, long targetUserId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ? ";
        return jdbcTemplate.update(sqlQuery, targetFilmId, targetUserId) > 0;
    }

    @Override
    public ArrayList<Film> findPopularFilms(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count не может быть отрицательным.");
        }
        String sqlQuery = "SELECT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE, FILMS.DURATION, " +
                "FILMS.RATING_ID, RATINGS.NAME, GENRES.ID, GENRES.NAME, COUNT(LIKES.FILM_ID) FROM FILMS JOIN RATINGS ON FILMS.RATING_ID = RATINGS.ID " +
                "LEFT JOIN FILM_GENRES on FILMS.ID = FILM_GENRES.FILM_ID " +
                "LEFT JOIN GENRES on FILM_GENRES.GENRE_ID = GENRES.ID " +
                "LEFT JOIN LIKES on FILMS.ID = LIKES.FILM_ID " +
                "GROUP BY FILMS.ID " +
                "ORDER BY COUNT(LIKES.FILM_ID) DESC LIMIT " + count; //как вставить сюда count

        ArrayList<Film> arrayFilms = (ArrayList<Film>) jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        return arrayFilms;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film(resultSet.getLong("FILMS.ID"),
                resultSet.getString("FILMS.NAME"),
                resultSet.getString("FILMS.DESCRIPTION"),
                resultSet.getDate("FILMS.RELEASE_DATE").toLocalDate(),
                resultSet.getInt("FILMS.DURATION"),
                new Mpa(resultSet.getInt("FILMS.RATING_ID"), resultSet.getString("RATINGS.NAME")), null, null
        );

        String check = resultSet.getString("GENRES.NAME");
        if (check == null || check.equals("")) {
            film.setGenres(Optional.of(new ArrayList<>()));
        } else {
            String sqlQuery2 = "SELECT FILM_GENRES.GENRE_ID, GENRES.NAME FROM FILM_GENRES " +
                    "LEFT JOIN GENRES on FILM_GENRES.GENRE_ID = GENRES.ID WHERE FILM_GENRES.FILM_ID =" + film.getId();
            ArrayList<Genre> newGenres = (ArrayList<Genre>) jdbcTemplate.query(sqlQuery2, this::mapRowToGenre);

            Set<Genre> set = new HashSet<>(newGenres);
            newGenres.clear();
            newGenres.addAll(set);

            film.setGenres(Optional.of(newGenres));
        }

        String sqlQuery2 = "SELECT USER_ID AS ID FROM LIKES WHERE FILM_ID = 2";
        ArrayList<Long> likes = (ArrayList<Long>) jdbcTemplate.query(sqlQuery2, this::mapRowToLong);
        Set<Long> likesSet = new HashSet<>(likes);
        film.setUserLikes(likesSet);

        return film;
    }

    private Long mapRowToLong(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("ID");
        return id;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre(resultSet.getInt("FILM_GENRES.GENRE_ID"), resultSet.getString("GENRES.NAME"));
        return genre;
    }
}
