package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public void addLike(Integer filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException(
                    String.format("Пользователь c id= %d не найден!", userId));
        }
        film.addLike(userId);
    }

    public void deleteLike(Integer filmId, Long userId) {

        Film film = filmStorage.getFilmById(filmId);
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException(
                    String.format("Пользователь c id= %d не найден!", userId));
        }
        film.deleteLike(userId);
    }
}