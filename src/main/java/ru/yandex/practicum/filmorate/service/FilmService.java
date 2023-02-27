package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private static final int MAX_LENGTH = 200;
    private static final LocalDate MIN_DATE = LocalDate.of(1895,12,28);
    private static final int TOP_LIMIT = 10;
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

    public List getAll() {
        return filmStorage.getFilms();
    }

    public Film getById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public List getTopFilms(Integer count) {
        if (count == null) count = TOP_LIMIT;
        return filmStorage.getTopFilms(count);
    }

    public void add(Film film) {
        isValid(film);
        filmStorage.addFilm(film);
    }

    public void addLike(Integer filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException(
                    String.format("Пользователь c id= %d не найден!", userId));
        }
        film.addLike(userId);
    }

    public Film update(Film film) {
        isValid(film);
        filmStorage.updateFilm(film);
        return film;
    }

    public void deleteLike(Integer filmId, Long userId) {

        Film film = filmStorage.getFilmById(filmId);
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException(
                    String.format("Пользователь c id= %d не найден!", userId));
        }
        film.deleteLike(userId);
    }
    public void isValid(Film film) { // используется в тестах, поэтому не может быть private
        if (film.getDescription().length() > MAX_LENGTH) {
            log.info("Ошибка валидации: Описание более 200 символов");
            throw new ValidationException("Описание более 200 символов");
        }
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.info("Ошибка валидации: Дата релиза не должна быть раньше 28/12/1895");
            throw new ValidationException("Дата релиза не должна быть раньше 28/12/1895");
        }
        if (film.getDuration() <= 0) {
            log.info("Ошибка валидации: Продолжительность фильма должна быть больше нуля");
            throw new ValidationException("Продолжительность фильма должна быть больше нуля");
        }
    }
}