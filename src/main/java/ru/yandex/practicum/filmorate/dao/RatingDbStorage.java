package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List getRatings() {
        String sql = "select rating_id, name from Rating;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeRating(rs));
    }

    @Override
    public Rating getRatingById(Integer id) {
        log.info("Check contains rating id={}", id);
        String sql = "select count(1) as row_count from Rating where rating_id = ?;";
        int rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt("row_count"), id);
        if (rowCount == 0) {
            log.error(String.format("Рейтинг c id= %d не найден!", id));
            throw new NotFoundException(
                    String.format("Рейтинг c id= %d не найден!", id));
        }
        sql = "select rating_id, name from Rating where rating_id = ?;";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeRating(rs), id);
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("rating_id");
        String name = rs.getString("name");

        return new Rating(id, name);
    }
}