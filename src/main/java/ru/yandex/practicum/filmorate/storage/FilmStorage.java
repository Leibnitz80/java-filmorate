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

    List getCommonFilms(Long userId, Long friendId);

    List<Film> getRecommendations(Long userId);
}