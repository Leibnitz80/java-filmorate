package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private static final String TOP_LIMIT = "10";
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List getAll() {
        log.info("Запрос: GET");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable("id") Integer id) {
        log.info("Запрос: GET by id");
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public List getTopFilms(@RequestParam(defaultValue = TOP_LIMIT, name = "count") Integer count,
                            @RequestParam(required = false, name = "genreId") Integer genreId,
                            @RequestParam(required = false, name = "year") Integer year) {
        log.info("Запрос: GET getTopFilms");
        return filmService.getTopFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Запрос: GET getCommonFilms");
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> getSearchFilms(@RequestParam String query,
                                     @RequestParam String by) {
        log.info("Запрос: GET getSearchFilms");
        return filmService.getSearchFilms(query, by);
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Запрос: POST {}", film);
        film = filmService.add(film);
        log.info("Запрос: POST обработан успешно");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Запрос: PUT update {}", film);
        film = filmService.update(film);
        log.info("Запрос: PUT update обработан успешно");
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Long userId) {
        log.info("Запрос: PUT addLike");
        filmService.addLike(filmId, userId);
        log.info("Запрос: PUT addLike обработан успешно");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Long userId) {
        log.info("Запрос: DELETE deleteLike");
        filmService.deleteLike(filmId, userId);
        log.info("Запрос: DELETE deleteLike обработан успешно");
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable("filmId") Integer id) {
        log.info("Запрос: DELETE deleteFilmById {}", id);
        filmService.deleteFilmById(id);
        log.info("Запрос: DELETE deleteFilmById {} обработан успешно", id);
    }

    @GetMapping("director/{directorId}")
    public Collection<Film> getFilmsByDirector(
            @PathVariable Integer directorId,
            @RequestParam(defaultValue = "year") String sortBy) {
        log.info("Запрос: getFilmsByDirector with condition");
        return filmService.getByDirectorId(directorId, sortBy);
    }

}