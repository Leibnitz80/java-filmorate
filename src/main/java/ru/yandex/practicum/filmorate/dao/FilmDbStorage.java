package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    private final MpaStorage mpaStorage;

    @Override
    public List<Film> getFilms() {
        String sql = "select f.film_id, f.name, f.description, f.releaseDate, f.duration, r.mpa_id, r.name as mpa_name " +
                "from Films f " +
                "    inner join Mpa r on r.mpa_id = f.mpa_id " +
                "order by f.film_id ;";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        sql = "select distinct gr.film_id, g.genre_id, g.name " +
                "from Genres_Relation gr " +
                "     inner join Genres g on g.genre_id = gr.genre_id " +
                "order by g.genre_id";
        jdbcTemplate.query(sql, (rx, rowNum) -> parseGenres(rx, films));
        return films;
    }

    private Genre parseGenres(ResultSet rs, List<Film> films) throws SQLException {
        int film_id;
        int genre_id;
        String name;
        film_id = rs.getInt("film_id");
        genre_id = rs.getInt("genre_id");
        name = rs.getString("name");
        Genre genre = new Genre(genre_id, name);
        for (Film film : films) {
            if (film.getId() == film_id) {
                film.getGenres().add(genre);
                break;
            }
        }
        return genre;
    }


    @Override
    public Film addFilm(Film film) {
        List<Genre> genresList = film.getGenres();
        String sqlQuery = "select count(1) as row_count from Films where name = ? and releaseDate = ?;";
        Long rowCount = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> rs.getLong("row_count"), film.getName(), film.getReleaseDate());
        if (rowCount > 0) return film;
        String sqlInsertQuery = "insert into Films(name, description, releaseDate, duration, mpa_id)" +
                "values(?,?,?,?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement prst = connection.prepareStatement(sqlInsertQuery, new String[]{"FILM_ID"});
            prst.setString(1, film.getName());
            prst.setString(2, film.getDescription());
            prst.setString(3, film.getReleaseDate().toString());
            prst.setInt(4, film.getDuration());
            prst.setInt(5, film.getMpa().getId());
            return prst;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());

        updateGenres(genresList, film.getId());
        return film;
    }

    @Override
    public Film get(long id) {
        log.debug("Extracting film with id={} from the database", id);
        String sql = "SELECT * FROM Films WHERE film_id=?";
        Film film = jdbcTemplate.query(sql, (rs, rowNum) -> createFilm(rs), id)
                .stream()
                .findFirst()
                .orElse(null);

        if (film != null) {
            String requestGenres = "SELECT genre_id FROM film_genre WHERE film_id=?";
            List<Genre> filmGenres = new ArrayList<>(jdbcTemplate.query(requestGenres, (rs, rowNum) -> extractGenre(rs), id));
            film.setGenres(filmGenres);
        }

        return film;
    }

    private Film createFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("title"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(mpaStorage.getMpaById(rs.getInt("rating_id")))
                .build();
    }

    private Genre extractGenre(ResultSet rs) throws SQLException {
        return genreStorage.getGenreById(rs.getInt("genre_id"));
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmContains(film.getId());
        String sql = "update Films " +
                "set name = ?," +
                "    description = ?," +
                "    releaseDate = ?," +
                "    duration = ?," +
                "    mpa_id = ? " +
                "where film_id = ?;";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film.getGenres(), film.getId());
        film = getFilmById(film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        checkFilmContains(id);
        String sql = "delete from Films where film_id = ?;";
        jdbcTemplate.update(sql, id);
        deleteGenres(id);
    }

    @Override
    public Film getFilmById(Integer id) {
        checkFilmContains(id);
        String sql = "select f.film_id, f.name, f.description, f.releaseDate, f.duration, r.mpa_id, r.name as mpa_name " +
                "from Films f " +
                "inner join Mpa r on r.mpa_id = f.mpa_id " +
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
        int[] updateCounts = jdbcTemplate.batchUpdate(
                "insert into Genres_relation(film_id, genre_id) " +
                        "select ?, ? " +
                        "where not exists (select 1 from Genres_relation where film_id = ? and genre_id = ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, genres.get(i).getId());
                        ps.setInt(3, filmId);
                        ps.setInt(4, genres.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    private void deleteGenres(Integer filmId) {
        String sql = "delete from Genres_relation where film_id = ?;";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        deleteLike(filmId, userId);
        String sql = "insert into Likes(film_id, user_id) " +
                "values(?,?); ";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Long userId) {
        String sql = "delete from Likes where film_id = ? and user_id = ?;";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        String sql = " select * " +
                " from Films " +
                " where film_id in (" +
                "    select RESULT.BESTUSERSFILMS as RECOMENDATION_FILM_ID " +
                "    from (select IFNULL(USERSFILMS.film_id, -1) as USERSFILMS, BESTUSERSFILMS.film_id as BESTUSERSFILMS " +
                "          from (select film_id from Likes where user_id = ?) as USERSFILMS " +
                "                   right join (select film_id " +
                "                               from Likes " +
                "                               where user_id IN (" +
                "                                   select BESTUSER.user_id " +
                "                                   from (select L2.user_id, COUNT(L2.user_id) " +
                "                                         from (select * from Likes where user_id = ?) as L1 " +
                "                                                  left join Likes as L2 on L1.film_id = L2.film_id " +
                "                                         where L2.user_id <> ? " +
                "                                         group by L2.user_id " +
                "                                         order by COUNT(L2.user_id) desc " +
                "                                         LIMIT 1) as BESTUSER)) as BESTUSERSFILMS " +
                "                              on USERSFILMS.film_id = BESTUSERSFILMS.film_id) as RESULT " +
                "    where RESULT.USERSFILMS = -1)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> this.getFilmById(rs.getInt("film_id")), id, id, id);
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
        Integer id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("releaseDate").toLocalDate();
        Integer duration = rs.getInt("duration");
        Mpa mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));

        String sql = "select distinct user_id from Likes where film_id = ? ;";
        List<Long> likes = jdbcTemplate.query(sql, (rz, rowNum) -> rz.getLong("user_id"), id);

        Film film = Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .genres(new ArrayList<>())
                .likes(likes)
                .build();
        return film;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");

        return new Genre(id, name);
    }
}