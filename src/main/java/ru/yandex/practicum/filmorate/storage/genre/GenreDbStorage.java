package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
public class GenreDbStorage implements GenreStorage {
    private static final String GET_ALL = "SELECT * FROM GENRES";
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(GET_ALL, this::mapRowToGenre);

    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        List<Genre> listGenre = getAllGenres();
        Map<Integer, Genre> mapGenre = new HashMap<>();
        for (Genre genre : listGenre) {
            mapGenre.put(genre.getId(), genre);
        }
        if (mapGenre.containsKey(id)) {
            return Optional.ofNullable(mapGenre.get(id));
        } else {
            return Optional.empty();
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("ID"), resultSet.getString("NAME"));
    }
}
