package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private HashMap<Integer,Film> films = new HashMap<>();
    private int currId = 0;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            isValid(film);
            if (film.getId() == 0) {
                film.setId(++currId);
            }
            films.put(film.getId(),film);
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            isValid(film);
            films.put(film.getId(),film);
        } else {
            throw new ValidationException("Несуществующий объект");
        }
        return film;
    }

    @GetMapping
    public List getFilms() {
        return new ArrayList<>(films.values());
    }

    public void isValid(Film film) {
        if (film.getName().isBlank()) {
            log.info("Название не должно быть пустым");
            throw new ValidationException("Название не должно быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.info("Описание более 200 символов");
            throw new ValidationException("Описание более 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.info("Дата релиза не должна быть раньше 28/12/1895");
            throw new ValidationException("Дата релиза не должна быть раньше 28/12/1895");
        }
        if (film.getDuration() <= 0) {
            log.info("Продолжительность фильма должна быть больше нуля");
            throw new ValidationException("Продолжительность фильма должна быть больше нуля");
        }
    }
}
