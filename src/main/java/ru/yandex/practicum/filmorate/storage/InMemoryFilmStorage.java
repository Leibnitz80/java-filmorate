package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currId;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        checkFilmContains(id);
        return films.get(id);
    }

    @Override
    public void addFilm(Film film) {
        film.setId(++currId);
        films.put(film.getId(),film);
    }

    @Override
    public void updateFilm(Film film) {
        checkFilmContains(film.getId());
        films.put(film.getId(),film);
        log.info("Запрос: PUT обработан успешно");
    }

    @Override
    public void deleteFilm(Integer id) {
        checkFilmContains(id);
        films.remove(id);
    }

    public void checkFilmContains(Integer id) {
        if (!films.containsKey(id)) {
            log.error(String.format("Фильм c id= %d не найден!", id));
            throw new NotFoundException(
                    String.format("Фильм c id= %d не найден!", id));
        }

    }
}