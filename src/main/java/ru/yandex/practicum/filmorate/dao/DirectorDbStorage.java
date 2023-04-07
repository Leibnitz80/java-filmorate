package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@Primary
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getDirectors() {
        String sql = "select director_id, name from Directors;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Directors")
                .usingGeneratedKeyColumns("director_id");

        director.setId(simpleJdbcInsert.executeAndReturnKey(this.directorToMap(director)).intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        final String sql = "update Directors set name = ? where director_id = ?";
        int count = jdbcTemplate.update(sql, director.getName(), director.getId());

        if (count == 1) {
            return director;
        } else {
            String message = String.format("Режиссер c id= %d не найден!", director.getId());
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public void deleteDirector(Integer id) {
        String sql = "delete from Directors where director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Director getDirectorById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select director_id, name from Directors where director_id =  ?", id);
        if (rs.next()) {
            return new Director(rs.getInt(1), rs.getString(2));
        }

        String message = String.format("Режиссер c id= %d не найден!", id);
        log.error(message);
        throw new NotFoundException(message);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("director_id");
        String name = rs.getString("name");
        return new Director(id, name);
    }

    private Map<String, Object> directorToMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        return values;
    }
}
