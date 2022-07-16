package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("filmDbStorage")
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID )" +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        Mpa mpa = new Mpa(0, null);
        SqlRowSet rateRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATINGS WHERE ID=" + film.getMpa().getId());
        if (rateRows.next()) {
            mpa.setId(rateRows.getInt("ID"));
            mpa.setName(rateRows.getString("NAME"));
        }
        film.setMpa(mpa);

        if (film.getGenres().isPresent()) {
            ArrayList<Genre> listGenre = film.getGenres().get();
            ArrayList<Genre> listGenreNew = new ArrayList<>();
            for (Genre g : listGenre) {
                SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE ID=" + g.getId());
                if (genreRows.next()) {
                    Genre fullGenre = new Genre(0, "");
                    fullGenre.setId(genreRows.getInt("ID"));
                    fullGenre.setName(genreRows.getString("NAME"));
                    listGenreNew.add(fullGenre);
                }
                film.setGenres(Optional.of(listGenreNew));

                jdbcTemplate.update(
                        "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID)" +
                                "VALUES (?, ?)", film.getId(), g.getId());
            }
        }
        return film;
    }

    @Override
    public boolean deleteFilm(Film film) {
        String sqlQuery = "DELETE FROM FILMS WHERE ID = ?";
        return jdbcTemplate.update(sqlQuery, film.getId()) > 0;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE FILMS SET " +
                "NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ?" +
                "WHERE ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        Mpa mpa = new Mpa(0, null);
        SqlRowSet rateRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATINGS WHERE ID=" + film.getMpa().getId());
        if (rateRows.next()) {
            mpa.setId(rateRows.getInt("ID"));
            mpa.setName(rateRows.getString("NAME"));
        }
        film.setMpa(mpa);

        if (film.getGenres().isPresent()) {
            jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID=?", film.getId()); //удаляем старые жанры

            ArrayList<Genre> listGenre = film.getGenres().get();
            ArrayList<Genre> listGenreNew = new ArrayList<>();
            for (Genre g : listGenre) {
                SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE ID=" + g.getId());
                if (genreRows.next()) {
                    Genre fullGenre = new Genre(0, "");
                    fullGenre.setId(genreRows.getInt("ID"));
                    fullGenre.setName(genreRows.getString("NAME"));
                    if (!listGenreNew.contains(fullGenre)) {
                        listGenreNew.add(fullGenre);
                    }

                }
                film.setGenres(Optional.of(listGenreNew));

                jdbcTemplate.update(
                        "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID)" +
                                "VALUES (?, ?)", film.getId(), g.getId());
            }
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

    @Override
    public Collection<Film> findAllFilms() {
        String sqlQuery = "SELECT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE, FILMS.DURATION, " +
                "FILMS.RATING_ID, RATINGS.NAME, GENRES.ID, GENRES.NAME FROM FILMS JOIN RATINGS ON FILMS.RATING_ID = RATINGS.ID " +
                "LEFT JOIN FILM_GENRES on FILMS.ID = FILM_GENRES.FILM_ID " +
                "LEFT JOIN GENRES on FILM_GENRES.GENRE_ID = GENRES.ID";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);


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

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre(resultSet.getInt("FILM_GENRES.GENRE_ID"), resultSet.getString("GENRES.NAME"));
        return genre;
    }

    @Override
    public Map<Long, Film> getFilms() {
        String sqlQuery = "SELECT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE, FILMS.DURATION, " +
                "FILMS.RATING_ID, RATINGS.NAME, GENRES.ID, GENRES.NAME FROM FILMS JOIN RATINGS ON FILMS.RATING_ID = RATINGS.ID " +
                "LEFT JOIN FILM_GENRES on FILMS.ID = FILM_GENRES.FILM_ID " +
                "LEFT JOIN GENRES on FILM_GENRES.GENRE_ID = GENRES.ID";

        List<Film> listFilm = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        Map<Long, Film> mapFilm = new HashMap<>();
        for (Film film : listFilm) {
            mapFilm.put(film.getId(), film);
        }
        return mapFilm;
    }
}
