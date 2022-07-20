package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
public class MpaDbStorage implements MpaStorage {
    private static final String GET_ALL = "SELECT * FROM RATINGS";
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpas() {
        return jdbcTemplate.query(GET_ALL, this::mapRowToMpa);
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        List<Mpa> listMpa = getAllMpas();
        Map<Integer, Mpa> mapMpa = new HashMap<>();
        for (Mpa mpa : listMpa) {
            mapMpa.put(mpa.getId(), mpa);
        }
        if (mapMpa.containsKey(id)) {
            return Optional.ofNullable(mapMpa.get(id));
        } else {
            return Optional.empty();
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("ID"), resultSet.getString("NAME"));
    }
}
