package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List getAll() {
        log.info("Запрос: GET getAll");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Long id) {
        log.info("Запрос: GET getById");
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public List getAllFriends(@PathVariable("id") Long id) {
        log.info("Запрос: GET getAllFriends");
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
        user = userService.add(user);
        log.info("Запрос: POST обработан успешно");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Запрос: PUT update {}", user);
        userService.update(user);
        log.info("Запрос: PUT update обработан успешно");
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

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") Long id) {
        log.info("Запрос: DELETE deleteUserById {}", id);
        userService.deleteUserById(id);
        log.info("Запрос: DELETE deleteUserById {} обработан успешно", id);
    }
}