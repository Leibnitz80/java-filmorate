package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private static final int MAX_LENGTH = 200;
    private static final LocalDate MIN_DATE = LocalDate.of(1895,12,28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int currId;

    @GetMapping
    public List getAll() {
        log.info("Запрос: GET");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Запрос: POST {}", film);
        isValid(film);
        film.setId(++currId);
        films.put(film.getId(),film);
        log.info("Запрос: POST обработан успешно");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Запрос: PUT {}", film);
        if (!films.containsKey(film.getId())) {
            log.info("Запрос: PUT обработан с ошибкой: Несуществующий объект");
            throw new ValidationException("Несуществующий объект");
        }
        isValid(film);
        films.put(film.getId(),film);
        log.info("Запрос: PUT обработан успешно");

        return film;
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