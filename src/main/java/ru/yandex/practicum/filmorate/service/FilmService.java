package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.ActionType;
import ru.yandex.practicum.filmorate.model.enums.ObjectType;
import ru.yandex.practicum.filmorate.model.enums.Search;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private static final int MAX_LENGTH = 200;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final UserService userService;

    public List<Film> getAll() {
        return filmStorage.getFilms();
    }

    public Film getById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getTopFilms(count, genreId, year);
    }

    public Film add(Film film) {
        isValid(film);
        film = filmStorage.addFilm(film);
        return film;
    }

    public void addLike(Integer filmId, Long userId) {
        userStorage.checkUserContains(userId);
        filmStorage.addLike(filmId, userId);
        userStorage.addUserEvent(userId, ObjectType.LIKE.name(), ActionType.ADD.name(), Long.valueOf(filmId));
    }

    public Film update(Film film) {
        isValid(film);
        film = filmStorage.updateFilm(film);
        return film;
    }

    public void deleteLike(Integer filmId, Long userId) {
        userStorage.checkUserContains(userId);
        filmStorage.deleteLike(filmId, userId);
        userStorage.addUserEvent(userId, ObjectType.LIKE.name(), ActionType.REMOVE.name(), Long.valueOf(filmId));
    }

    public List<Film> getByDirectorId(Integer id, String sortOrder) {
        List<Film> result = filmStorage.getByDirectorId(id,sortOrder);

        if (result == null || result.isEmpty()) {
            log.error("Ошибка: нет такого режиссера или фильмов с id режиссера " + id);
            throw new NotFoundException(
                    String.format("ошибка: нет такого режиссера или фильмов с id режиссера %d", id));
        }

        return result;
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        userStorage.checkUserContains(userId);
        userStorage.checkUserContains(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }


    public List<Film> getRecommendations(Long id) {
        userService.getById(id);
        return filmStorage.getRecommendations(id);
    }

    public List<Film> getSearchFilms(String query, String by) {
        List<Film> searchFilms = new ArrayList<>();
        String[] splitBy = by.split(",");

        try {
            switch (splitBy.length) {
                case 1:
                    Search search = Search.valueOf(by.toUpperCase());

                    if (search.equals(Search.TITLE)) {
                        searchFilms = filmStorage.getFilmsByTitle(query);
                    } else if (search.equals(Search.DIRECTOR)) {
                        searchFilms = filmStorage.getFilmsByDirector(query);
                    }
                    break;
                case 2:
                    for (String s : splitBy) {
                        Search.valueOf(s.toUpperCase());
                    }

                    searchFilms = filmStorage.getFilmsAnywayByTitle(query);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неправильный @RequestParam: " + by);
        }

        return searchFilms;
    }

    public void deleteFilmById(Integer id) {
        filmStorage.deleteFilm(id);
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
        if (film.getMpa().getId() < 1) {
            log.info("Ошибка валидации: mpa_id должен быть больше 0");
            throw new ValidationException("mpa_id должен быть больше 0");
        }
        if (!film.getGenres().isEmpty()) {
            if (film.getGenres().stream().noneMatch(genre -> genre.getId() > 0)) {
                log.info("Ошибка валидации: genre_id должен быть больше 0");
                throw new ValidationException("genre_id должен быть больше 0");
            }
        }
    }
}