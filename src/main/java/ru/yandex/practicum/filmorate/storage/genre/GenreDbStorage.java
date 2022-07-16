package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAllGenres() {
        String sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);

    }

    @Override
    public Map<Integer, Genre> getGenres() {
        String sqlQuery = "SELECT * FROM GENRES";
        List<Genre> listGenre = jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
        Map<Integer, Genre> mapGenre = new HashMap<>();
        for (Genre genre : listGenre) {
            mapGenre.put(genre.getId(), genre);
        }
        return mapGenre;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre(resultSet.getInt("ID"), resultSet.getString("NAME"));
        return genre;
    }
}
