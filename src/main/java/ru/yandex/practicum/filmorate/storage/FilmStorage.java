package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Integer id);

    Film getFilmById(Integer id);

    void addLike(Integer filmId, Long userId);

    void deleteLike(Integer filmId, Long userId);

    List<Film> getByDirectorId(Integer id);

    List getCommonFilms(Long userId, Long friendId);

    void checkFilmContains(Integer id);

    List<Film> getRecommendations(Long userId);

    List<Film> getFilmsByTitle(String query);

    List<Film> getFilmsByDirector(String query);

    List<Film> getFilmsAnywayByTitle(String query);
    List<Film> getTopFilms(Integer count, Integer genreId, Integer year);
}