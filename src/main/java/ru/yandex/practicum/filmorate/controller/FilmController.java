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
        log.info("Запрос для Film: GET getAll");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable("id") Integer id) {
        log.info("Запрос для Film: GET getById {}", id);
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public List getTopFilms(@RequestParam(defaultValue = TOP_LIMIT, name = "count") Integer count,
                            @RequestParam(required = false, name = "genreId") Integer genreId,
                            @RequestParam(required = false, name = "year") Integer year) {
        log.info("Запрос для Film: GET getTopFilms {} {} {}", count, genreId, year);
        return filmService.getTopFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Запрос для Film: GET getCommonFilms {} {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> getSearchFilms(@RequestParam String query,
                                     @RequestParam String by) {
        log.info("Запрос для Film: GET getSearchFilms {} {}", query, by);
        return filmService.getSearchFilms(query, by);
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Запрос для Film: POST add {}", film);
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Запрос для Film: PUT update {}", film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Long userId) {
        log.info("Запрос для Film: PUT addLike {} {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Long userId) {
        log.info("Запрос для Film: DELETE deleteLike {} {}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable("filmId") Integer id) {
        log.info("Запрос для Film: DELETE deleteFilmById {}", id);
        filmService.deleteFilmById(id);
    }

    @GetMapping("director/{directorId}")
    public Collection<Film> getFilmsByDirector(
            @PathVariable Integer directorId,
            @RequestParam(defaultValue = "year") String sortBy) {
        log.info("Запрос для Film: GET getFilmsByDirector {} {}", directorId, sortBy);
        return filmService.getByDirectorId(directorId, sortBy);
    }

}