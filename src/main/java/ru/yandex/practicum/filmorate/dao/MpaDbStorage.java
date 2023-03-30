package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List getMpas() {
        String sql = "select mpa_id, name from Mpa;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Mpa getMpaById(Integer id) {
        log.info("Check contains mpa id={}", id);
        String sql = "select count(1) as row_count from Mpa where mpa_id = ?;";
        int rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt("row_count"), id);
        if (rowCount == 0) {
            log.error(String.format("Рейтинг c id= %d не найден!", id));
            throw new NotFoundException(
                    String.format("Рейтинг c id= %d не найден!", id));
        }
        sql = "select mpa_id, name from Mpa where mpa_id = ?;";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), id);
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("mpa_id");
        String name = rs.getString("name");

        return new Mpa(id, name);
    }
}