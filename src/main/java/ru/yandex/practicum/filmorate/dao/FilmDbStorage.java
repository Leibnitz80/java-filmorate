package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select f.film_id, f.name, f.description, f.releaseDate, f.duration, r.rating_id, r.name as mpa_name " +
                "from Films f " +
                "    inner join Rating r on r.rating_id = f.rating_id " +
                "order by f.film_id ;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addFilm(Film film) {
        List<Genre> genresList = film.getGenres();
        String sql = "select count(1) as row_count from Films where name = ? and releaseDate = ?;";
        Long rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("row_count"), film.getName(), film.getReleaseDate());
        if (rowCount == 0) {
            sql = "insert into Films(name, description, releaseDate, duration, rating_id)" +
                  "values(?,?,?,?,?);";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        }
        sql = "select f.film_id, f.name, f.description, f.releaseDate, f.duration, r.rating_id, r.name as mpa_name " +
              "from Films f " +
              "    inner join Rating r on r.rating_id = f.rating_id " +
              "where f.name = ? and f.releaseDate = ? ;";
        film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), film.getName(), film.getReleaseDate());
        updateGenres(genresList, film.getId());
        film.setGenres(getFilmGenres(film.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmContains(film.getId());
        String sql = "update Films " +
                     "set name = ?," +
                     "    description = ?," +
                     "    releaseDate = ?," +
                     "    duration = ?," +
                     "    rating_id = ? " +
                     "where film_id = ?;";
        jdbcTemplate.update(sql,film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film.getGenres(), film.getId());
        film = getFilmById(film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        checkFilmContains(id);
        String sql = "delete from Films where film_id = ?;";
        jdbcTemplate.update(sql,id);
        deleteGenres(id);
    }

    @Override
    public Film getFilmById(Integer id) {
        checkFilmContains(id);
        String sql = "select f.film_id, f.name, f.description, f.releaseDate, f.duration, r.rating_id, r.name as mpa_name " +
                     "from Films f " +
                     "inner join Rating r on r.rating_id = f.rating_id " +
                "where f.film_id = ?;";
        Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        film.setGenres(getFilmGenres(id));
        return film;
    }

    public List<Genre> getFilmGenres(Integer film_id) {
        String sql = "select g.genre_id, g.name from Genres_relation gr " +
                     "               inner join genres g on g.genre_id = gr.genre_id " +
                     "where film_id = ? " +
                     "order by g.genre_id ;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), film_id);
    }

    private void updateGenres(List<Genre> genres, Integer filmId) {
        deleteGenres(filmId);
        for (int i = 0; i < genres.size(); i++) {
            String sql = "select count(1) as row_count from Genres_relation where film_id = ? and genre_id = ?;";
            Integer rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt("row_count"), filmId, genres.get(i).getId());
            if (rowCount == 0) {
                sql = "insert into Genres_relation(film_id, genre_id) " +
                      "values(?,?) ";
                jdbcTemplate.update(sql,filmId, genres.get(i).getId());
            }
        }
    }

    private void deleteGenres(Integer filmId) {
        String sql = "delete from Genres_relation where film_id = ?;";
        jdbcTemplate.update(sql,filmId);
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        deleteLike(filmId, userId);
        String sql = "insert into Likes(film_id, user_id) " +
                     "values(?,?); ";
        jdbcTemplate.update(sql,filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Long userId) {
        String sql = "delete from Likes where film_id = ? and user_id = ?;";
        jdbcTemplate.update(sql,filmId,userId);
    }

    public void checkFilmContains(Integer id) {
        String sql = "select count(1) as row_count from Films where film_id = ?;";
        Long rowCount = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("row_count"), id);
        if (rowCount == 0) {
            log.error(String.format("Фильм c id= %d не найден!", id));
            throw new NotFoundException(
                    String.format("Фильм c id= %d не найден!", id));
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        // используем конструктор, методы ResultSet
        // и готовое значение user
        Integer id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        // Получаем дату и конвертируем её из sql.Date в time.LocalDate
        LocalDate releaseDate = rs.getDate("releaseDate").toLocalDate();
        Integer duration = rs.getInt("duration");
        Mpa mpa = new Mpa(rs.getInt("rating_id"),rs.getString("mpa_name"));

        String sql = "select g.genre_id, g.name " +
                     "from Genres_Relation gr " +
                     "     inner join Genres g on g.genre_id = gr.genre_id " +
                     "where gr.film_id = ? ;";
        List<Genre> genres = jdbcTemplate.query(sql, (rx, rowNum) -> makeGenre(rx), id);
        sql = "select distinct user_id from Likes where film_id = ? ;";
        List<Long> likes = jdbcTemplate.query(sql, (rz, rowNum) -> rz.getLong("user_id"), id);

        return new Film(id, name, description, releaseDate, duration, mpa, genres, likes);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");

        return new Genre(id, name);
    }
}