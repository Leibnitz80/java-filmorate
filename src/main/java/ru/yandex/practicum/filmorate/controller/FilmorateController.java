package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Filmorable;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping
abstract class FilmorateController<T extends Filmorable> {
    private final Map<Integer, T> objs = new HashMap<>();
    private int currId;

    @GetMapping
    public List getAll() {
        log.info("Запрос: GET");
        return new ArrayList<>(objs.values());
    }

    @PostMapping
    public T addUser(@Valid @RequestBody T obj) {
        log.info("Запрос: POST {}", obj);
        isValid(obj);
        obj.setId(++currId);
        objs.put(obj.getId(),obj);
        log.info("Запрос: POST обработан успешно");
        return obj;
    }

    @PutMapping
    public T updateUser(@Valid @RequestBody T obj) {
        log.info("Запрос: PUT {}", obj);
        if (!objs.containsKey(obj.getId())) {
            log.info("Запрос: PUT обработан с ошибкой: Несуществующий объект");
            throw new ValidationException("Несуществующий объект");
        }
        isValid(obj);
        objs.put(obj.getId(),obj);
        log.info("Запрос: PUT обработан успешно");

        return obj;
    }

    public void isValid(T obj) {
    }
}