package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private static final int MAX_LENGTH = 200;
    private static final LocalDate MIN_DATE = LocalDate.of(1895,12,28);
    private static final String TOP_LIMIT = "10";

    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
        this.filmStorage = filmService.getFilmStorage();
    }

    @GetMapping
    public List getAll() {
        log.info("Запрос: GET");
        return filmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Integer id) {
        log.info("Запрос: GET by id");
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List getTopFilms(@RequestParam(defaultValue = TOP_LIMIT, required = false) Integer count) {
        log.info("Запрос: GET getTopFilms");
        return filmStorage.getTopFilms(count);
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Запрос: POST {}", film);
        isValid(film);
        filmStorage.addFilm(film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Запрос: PUT {}", film);
        isValid(film);
        filmStorage.updateFilm(film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Long userId) {
        log.info("Запрос: PUT addLike");
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Long userId) {
        log.info("Запрос: DELETE deleteLike");
        filmService.deleteLike(filmId, userId);
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