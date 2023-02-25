package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        this.userStorage = userService.getUserStorage();
    }

    @GetMapping
    public List getAll() {
        log.info("Запрос: GET");
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        log.info("Запрос: GET");
        return userStorage.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List getAllFriends(@PathVariable("id") Long id) {
        log.info("Запрос: GET getFriends");
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List getCommonFriends(@PathVariable("id") Long id1, @PathVariable("otherId") Long id2) {
        log.info("Запрос: GET getCommonFriends");
        return userService.getCommonFriends(id1, id2);
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Запрос: POST {}", user);
        isValid(user);
        userStorage.addUser(user);
        log.info("Запрос: POST обработан успешно");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Запрос: PUT {}", user);
        isValid(user);
        userStorage.updateUser(user);
        log.info("Запрос: PUT обработан успешно");
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void makeFriends(@PathVariable("id") Long id1, @PathVariable("friendId") Long id2) {
        log.info("Запрос: PUT makeFriends");
        userService.makeFriends(id1, id2);
        log.info("Запрос: PUT makeFriends обработан успешно");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable("id") Long id1, @PathVariable("friendId") Long id2) {
        log.info("Запрос: DELETE deleteFriends");
        userService.deleteFriends(id1, id2);
        log.info("Запрос: DELETE deleteFriends обработан успешно");
    }

    public void isValid(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Ошибка валидации: День рождения не должен быть в будущем");
            throw new ValidationException("День рождения не должен быть в будущем");
        }
        if (user.getLogin().contains(" ")) {
            log.info("Ошибка валидации: В логине не должно быть пробелов");
            throw new ValidationException("В логине не должно быть пробелов");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}