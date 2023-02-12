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
    private Map<Integer, User> users = new HashMap<>();
    private int currId = 0;

    @GetMapping
    public Collection getUsers() {
        log.info("GET");
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            isValid(user);
            if (user.getId() == 0) {
                user.setId(++currId);
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(),user);
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        isValid(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(),user);
        } else {
            throw new ValidationException("Несуществующий объект");
        }

        return user;
    }

    public void isValid(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Некорректный email");
            throw new ValidationException("Некорректный email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("Некорректный логин");
            throw new ValidationException("Некорректный логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("День рождения не должен быть в будущем");
            throw new ValidationException("День рождения не должен быть в будущем");
        }
    }
}