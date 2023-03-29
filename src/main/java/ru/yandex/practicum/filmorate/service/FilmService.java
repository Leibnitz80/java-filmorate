package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private static final int MAX_LENGTH = 200;
    private static final LocalDate MIN_DATE = LocalDate.of(1895,12,28);
    private static final Comparator<Film> COMP_BY_LIKES = (p0, p1) -> {
        return p1.getLikesCount() - p0.getLikesCount();
    };
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAll() {
        return filmStorage.getFilms();
    }

    public Film getById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getTopFilms(Integer count) {
        return getAll().stream()
                .sorted(COMP_BY_LIKES)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film add(Film film) {
        isValid(film);
        film = filmStorage.addFilm(film);
        return film;
    }

    public void addLike(Integer filmId, Long userId) {
        userStorage.checkUserContains(userId);
        filmStorage.addLike(filmId, userId);
    }

    public Film update(Film film) {
        isValid(film);
        film = filmStorage.updateFilm(film);
        return film;
    }

    public void deleteLike(Integer filmId, Long userId) {
        userStorage.checkUserContains(userId);
        filmStorage.deleteLike(filmId,userId);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        userStorage.checkUserContains(userId);
        userStorage.checkUserContains(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void isValid(Film film) { // используется в тестах, поэтому не может быть private
        if (film.getName().isBlank()) {
            log.info("Ошибка валидации: Пустое наименование фильма");
            throw new ValidationException("Пустое наименование фильма");
        }
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