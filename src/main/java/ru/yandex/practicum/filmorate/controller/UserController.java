package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int currId;

    @GetMapping
    public List getUsers() {
        log.info("Запрос: GET /users");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Запрос: POST /users");
        log.info(user.toString());
        isValid(user);
        user.setId(++currId);
        users.put(user.getId(),user);
        log.info("Запрос: POST /users обработан успешно");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос: PUT /users");
        log.info(user.toString());
        if (!users.containsKey(user.getId())) {
            log.info("Запрос: PUT /users обработан с ошибкой: Несуществующий объект");
            throw new ValidationException("Несуществующий объект");
        }
        isValid(user);
        users.put(user.getId(),user);
        log.info("Запрос: PUT /users обработан успешно");

        return user;
    }

    public void isValid(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Ошибка валидации: День рождения не должен быть в будущем");
            throw new ValidationException("День рождения не должен быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}