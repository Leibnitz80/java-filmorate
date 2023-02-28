package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List getFilms();
    void addFilm(Film film);
    void updateFilm(Film film);
    void deleteFilm(Integer id);
    Film getFilmById(Integer ig);
}