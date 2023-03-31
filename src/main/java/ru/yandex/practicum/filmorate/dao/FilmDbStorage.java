package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select f.film_id, f.name, f.description, f.releaseDate, f.duration, r.mpa_id, r.name as mpa_name " +
                     "from Films f " +
                     "inner join Mpa r on r.mpa_id = f.mpa_id " +
                     "order by f.film_id ;";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        sql = "select distinct gr.film_id, g.genre_id, g.name " +
              "from Genres_Relation gr " +
              "inner join Genres g on g.genre_id = gr.genre_id " +
               "order by g.genre_id";
        jdbcTemplate.query(sql, (rx, rowNum) -> parseGenres(rx, films));
        getDirectorsIntoFilms(films);
        return films;
    }

    private Genre parseGenres(ResultSet rs, List<Film> films) throws SQLException {
        int filmId;
        int genreId;
        String name;
        filmId = rs.getInt("film_id");
        genreId = rs.getInt("genre_id");
        name = rs.getString("name");
        Genre genre = new Genre(genreId, name);
        for (Film film : films) {
            if (film.getId() == filmId) {
                film.getGenres().add(genre);
                break;
            }
        }
        return genre;
    }


    @Override
    public Film addFilm(Film film) {
        List<Genre> genresList = film.getGenres();
        List<Director> directorsList = film.getDirectors();
        String sqlQuery = "select count(1) as row_count from Films where name = ? and releaseDate = ? and description = ?;";
        Long rowCount = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> rs.getLong("row_count"), film.getName(), film.getReleaseDate(), film.getDescription());
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
        updateDirectorsInFilm(directorsList, film.getId());
        updateGenres(genresList, film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmContains(film.getId());
        String sql = "update Films " +
                     "set name = ?," +
                     "description = ?," +
                     "releaseDate = ?," +
                     "duration = ?," +
                     "mpa_id = ? " +
                     "where film_id = ?;";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateDirectorsInFilm(film.getDirectors(), film.getId());
        updateGenres(film.getGenres(), film.getId());
        film = getFilmById(film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        checkFilmContains(id);
        String sql = "delete from Films where film_id = ?;";
        jdbcTemplate.update(sql, id);

        // Удаляю оставшийся film_id из всех связанных таблиц
        deleteDirectorsInFilm(id);
        deleteFilmFromLikes(id);
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
        film.setDirectors(getFilmDirectors(id));
        return film;
    }

    public List<Genre> getFilmGenres(Integer filmId) {
        String sql = "select g.genre_id, g.name from Genres_relation gr " +
                     "inner join genres g on g.genre_id = gr.genre_id " +
                     "where film_id = ? " +
                     "order by g.genre_id ;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql =
                "select f.film_id, f.name, f.description, f.releaseDate, f.duration, r.mpa_id, r.name as mpa_name, count(f.film_id) " +
                "from Films f " +
                "inner join Mpa r on r.mpa_id = f.mpa_id " +
                "inner join Likes ul on ul.film_id = f.film_id and ul.user_id = ? " +
                "inner join Likes fl on fl.film_id = f.film_id and fl.user_id = ? " +
                "group by f.film_id " +
                "order by count(f.film_id) asc";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), userId, friendId);

        sql = "select distinct gr.film_id, g.genre_id, g.name " +
              "from Genres_Relation gr " +
              "inner join Genres g on g.genre_id = gr.genre_id " +
              "order by g.genre_id";
        jdbcTemplate.query(sql, (rx, rowNum) -> parseGenres(rx, films));
        return films;
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
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
        return jdbcTemplate.query(sql, (rs, rowNum) -> this.getFilmById(rs.getInt("film_id")), userId, userId, userId);
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
                }
        );
    }

    private void deleteGenres(Integer filmId) {
        String sql = "delete from Genres_relation where film_id = ?;";
        jdbcTemplate.update(sql, filmId);
        log.info("Все записи с film_id {} удалены из таблицы Genres_relation. ", filmId);
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
    public List<Film> getByDirectorId(Integer id) {
        String sql = "select f.film_id, f.name, f.description, f.releaseDate, f.duration, r.mpa_id, r.name as mpa_name, dr.director_id " +
                     "from Films f " +
                     "inner join directors_relation dr on dr.film_id = f.film_id " +
                     "inner join Mpa r on r.mpa_id = f.mpa_id " +
                     "where dr.director_id = ? " +
                     "order by f.film_id;";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        sql = "select distinct gr.film_id, g.genre_id, g.name " +
              "from Genres_Relation gr " +
              "inner join Genres g on g.genre_id = gr.genre_id " +
              "order by g.genre_id";
        jdbcTemplate.query(sql, (rx, rowNum) -> parseGenres(rx, films));
        getDirectorsIntoFilms(films);
        return films.stream().filter(p -> !p.getDirectors().isEmpty()).collect(Collectors.toList());
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

    private void deleteFilmFromLikes(Integer id) {
        String sql = "delete from Likes where film_id = ?;";
        jdbcTemplate.update(sql, id);
        log.info("Все записи с film_id {} удалены из таблицы likes. ", id);
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
        Mpa mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));

        String sql = "select distinct user_id from Likes where film_id = ? ;";
        List<Long> likes = jdbcTemplate.query(sql, (rz, rowNum) -> rz.getLong("user_id"), id);
        List<Director> directors = new ArrayList<>();
        Film film = Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .genres(new ArrayList<>())
                .likes(likes)
                .directors(directors)
                .build();
        return film;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");

        return new Genre(id, name);
    }

    private void updateDirectorsInFilm(List<Director> directors, Integer filmId) {
        deleteDirectorsInFilm(filmId);

        if (directors == null) {
            return;
        }

        int[] updateCounts = jdbcTemplate.batchUpdate(
                "insert into Directors_Relation(film_id, director_id) " +
                    "select ?, ? " +
                    "where not exists (select 1 from Directors_Relation where film_id = ? and director_id = ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, directors.get(i).getId());
                        ps.setInt(3, filmId);
                        ps.setInt(4, directors.get(i).getId());
                    }

                    public int getBatchSize() {
                        return directors.size();
                    }
                });
    }

    private void deleteDirectorsInFilm(Integer filmId) {
        String sql = "delete from Directors_Relation where film_id = ?;";
        jdbcTemplate.update(sql, filmId);
    }

    private void getDirectorsIntoFilms(List<Film> films) {
        String sql = "select distinct dr.film_id, d.director_id, d.name " +
                "from directors_relation dr " +
                "inner join directors d on d.director_id = dr.director_id " +
                "order by d.director_id";
        jdbcTemplate.query(sql, (rx, rowNum) -> parseDirectors(rx, films));
    }

    private Director parseDirectors(ResultSet rs, List<Film> films) throws SQLException {
        int filmId = rs.getInt("film_id");
        int directorId = rs.getInt("director_id");
        String name = rs.getString("name");
        Director director = new Director(directorId, name);
        for (Film film : films) {
            if (film.getId() == filmId) {
                film.getDirectors().add(director);
                break;
            }
        }
        return director;
    }

    private List<Director> getFilmDirectors(Integer filmId) {
        String sql = "select d.director_id, d.name from directors_relation dr " +
                     "inner join directors d on d.director_id = dr.director_id " +
                     "where film_id = ? " +
                     "order by d.director_id ;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), filmId);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("director_id");
        String name = rs.getString("name");

        return new Director(id, name);
    }
}