package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAllMpas() {
        String sqlQuery = "SELECT * FROM RATINGS";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Map<Integer, Mpa> getMpas() {
        String sqlQuery = "SELECT * FROM RATINGS";
        List<Mpa> listMpa = jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
        Map<Integer, Mpa> mapMpa = new HashMap<>();
        for (Mpa mpa : listMpa) {
            mapMpa.put(mpa.getId(), mpa);
        }
        return mapMpa;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(resultSet.getInt("ID"), resultSet.getString("NAME"));
        return mpa;
    }
}
