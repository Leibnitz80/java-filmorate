package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currId;

    @Override
    public List getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List getTopFilms(Integer count) { //эти данные логичнее забирать из хранилища
        return films.values().stream().sorted((p0, p1) -> {
            int comp;
            if(p0.getLikes().size() > p1.getLikes().size()){
                comp = -1;
            } else if (p0.getLikes().size() < p1.getLikes().size()) {
                comp = 1;
            } else {
                comp = 0;
            }
            return comp;
        }).limit(count).collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            log.info("Запрос: GET обработан с ошибкой: Несуществующий объект");
            throw new NotFoundException(
                    String.format("Фильм c id= %d не найден!", id));
        }
        return films.get(id);
    }

    @Override
    public void addFilm(Film film) {
        film.setId(++currId);
        films.put(film.getId(),film);
        log.info("Запрос: POST обработан успешно");
    }

    @Override
    public void updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Запрос: PUT обработан с ошибкой: Несуществующий объект");
            throw new NotFoundException("Попытка обновить несуществующий объект!");
        }
        films.put(film.getId(),film);
        log.info("Запрос: PUT обработан успешно");
    }

    @Override
    public void deleteFilm(Integer id) {
        if (!films.containsKey(id)) {
            log.info("Запрос: DELETE обработан с ошибкой: Несуществующий объект");
            throw new NotFoundException(
                    String.format("Фильм c id= %d не найден!", id));
        }
        films.remove(id);
    }
}