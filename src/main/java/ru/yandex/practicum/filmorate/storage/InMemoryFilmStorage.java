package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

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
    public Film addFilm(Film film) {
        film.setId(++currId);
        films.put(film.getId(),film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmContains(film.getId());
        films.put(film.getId(),film);
        log.info("Запрос: PUT обработан успешно");
        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        checkFilmContains(id);
        films.remove(id);
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        films.get(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Long userId) {
        films.get(filmId).deleteLike(userId);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> userFilms =
            films.values().stream().filter(f -> f.getLikes().contains(userId)).collect(Collectors.toList());
        List<Film> friendFilms =
            films.values().stream().filter(f -> f.getLikes().contains(friendId)).collect(Collectors.toList());
        return userFilms.stream()
                .filter(friendFilms::contains)
                .collect(Collectors.toList());
    }

    public void checkFilmContains(Integer id) {
        if (!films.containsKey(id)) {
            log.error(String.format("Фильм c id= %d не найден!", id));
            throw new NotFoundException(
                    String.format("Фильм c id= %d не найден!", id));
        }
    }
}