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

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private static final String ADD_FILM = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID )" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String GET_RATING = "SELECT * FROM RATINGS WHERE ID= %d";
    private static final String GET_GENRE = "SELECT * FROM GENRES WHERE ID= %d";
    private static final String ADD_GENRES = "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID)" +
            "VALUES (?, ?)";
    private static final String UPDATE_FILM = "UPDATE FILMS SET " +
            "NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ?" +
            "WHERE ID = ?";
    private static final String DELETE_GENRES = "DELETE FROM FILM_GENRES WHERE FILM_ID=?";
    private static final String GET_LIKES = "SELECT USER_ID AS ID FROM LIKES WHERE FILM_ID = %d";
    private static final String DELETE_FILM = "DELETE FROM FILMS WHERE ID = ?";
    private static final String GET_FILMS = "SELECT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE, " +
            "FILMS.DURATION, FILMS.RATING_ID, RATINGS.NAME FROM FILMS JOIN RATINGS ON FILMS.RATING_ID = RATINGS.ID";
    private static final String GET_GENRES = "SELECT FILM_GENRES.GENRE_ID, GENRES.NAME FROM FILM_GENRES " +
            "LEFT JOIN GENRES on FILM_GENRES.GENRE_ID = GENRES.ID WHERE FILM_GENRES.FILM_ID = %d";
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(ADD_FILM, new String[]{"ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        Mpa mpa = new Mpa(0, null);
        SqlRowSet rateRows = jdbcTemplate.queryForRowSet(String.format(GET_RATING, film.getMpa().getId()));
        if (rateRows.next()) {
            mpa.setId(rateRows.getInt("ID"));
            mpa.setName(rateRows.getString("NAME"));
        }
        film.setMpa(mpa);

        if (film.getGenres().isPresent()) {
            ArrayList<Genre> listGenre = film.getGenres().get();
            ArrayList<Genre> listGenreNew = new ArrayList<>();
            for (Genre genre : listGenre) {
                SqlRowSet genreRows = jdbcTemplate.queryForRowSet(String.format(GET_GENRE, genre.getId()));
                if (genreRows.next()) {
                    Genre fullGenre = new Genre(0, "");
                    fullGenre.setId(genreRows.getInt("ID"));
                    fullGenre.setName(genreRows.getString("NAME"));
                    listGenreNew.add(fullGenre);
                }
                Set<Genre> set = new HashSet<>(listGenreNew);
                listGenreNew.clear();
                listGenreNew.addAll(set);

                film.setGenres(Optional.of(listGenreNew));

                jdbcTemplate.update(ADD_GENRES, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        Mpa mpa = new Mpa(0, null);
        SqlRowSet rateRows = jdbcTemplate.queryForRowSet(String.format(GET_RATING, film.getMpa().getId()));
        if (rateRows.next()) {
            mpa.setId(rateRows.getInt("ID"));
            mpa.setName(rateRows.getString("NAME"));
        }
        film.setMpa(mpa);

        if (film.getGenres().isPresent()) {
            jdbcTemplate.update(DELETE_GENRES, film.getId()); //удаляем старые жанры

            ArrayList<Genre> listGenre = film.getGenres().get();
            ArrayList<Genre> listGenreNew = new ArrayList<>();
            for (Genre genre : listGenre) {
                SqlRowSet genreRows = jdbcTemplate.queryForRowSet(String.format(GET_GENRE, genre.getId()));
                if (genreRows.next()) {
                    Genre fullGenre = new Genre(0, "");
                    fullGenre.setId(genreRows.getInt("ID"));
                    fullGenre.setName(genreRows.getString("NAME"));
                    listGenreNew.add(fullGenre);
                }
                Set<Genre> set = new HashSet<>(listGenreNew);
                listGenreNew.clear();
                listGenreNew.addAll(set);

                film.setGenres(Optional.of(listGenreNew));

                jdbcTemplate.update(ADD_GENRES, film.getId(), genre.getId());
            }
        }

        List<Long> likes = jdbcTemplate.query(String.format(GET_LIKES, film.getId()), this::mapRowToLong);
        Set<Long> likesSet = new HashSet<>(likes);
        film.setUserLikes(likesSet);

        return film;
    }


    @Override
    public void deleteFilm(long id) {
        jdbcTemplate.update(DELETE_FILM, id);
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(GET_FILMS, this::mapRowToFilm);


    }

    @Override
    public Optional<Film> getFilmById(long id) {
        List<Film> listFilm = jdbcTemplate.query(GET_FILMS, this::mapRowToFilm);
        Map<Long, Film> mapFilm = new HashMap<>();
        for (Film film : listFilm) {
            mapFilm.put(film.getId(), film);
        }
        if (mapFilm.containsKey(id)) {
            return Optional.ofNullable(mapFilm.get(id));
        } else {
            return Optional.empty();
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film(resultSet.getLong("FILMS.ID"),
                resultSet.getString("FILMS.NAME"),
                resultSet.getString("FILMS.DESCRIPTION"),
                resultSet.getDate("FILMS.RELEASE_DATE").toLocalDate(),
                resultSet.getInt("FILMS.DURATION"),
                new Mpa(resultSet.getInt("FILMS.RATING_ID"), resultSet.getString("RATINGS.NAME")), null, null
        );

        ArrayList<Genre> newGenres = (ArrayList<Genre>) jdbcTemplate.query(String.format(
                GET_GENRES, film.getId()), this::mapRowToGenre);
        Set<Genre> set = new HashSet<>(newGenres);
        newGenres.clear();
        newGenres.addAll(set);
        film.setGenres(Optional.of(newGenres));

        ArrayList<Long> likes = (ArrayList<Long>) jdbcTemplate.query(String.format(
                GET_LIKES, film.getId()), this::mapRowToLong);
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
