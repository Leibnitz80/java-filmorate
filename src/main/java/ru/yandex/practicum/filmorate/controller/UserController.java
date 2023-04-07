package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @GetMapping
    public List getAll() {
        log.info("Запрос для User: GET getAll");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Long id) {
        log.info("Запрос для User: GET getById {}", id);
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public List getAllFriends(@PathVariable("id") Long id) {
        log.info("Запрос для User: GET getAllFriends {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List getCommonFriends(@PathVariable("id") Long id1, @PathVariable("otherId") Long id2) {
        log.info("Запрос для User: GET getCommonFriends {} {}", id1, id2);
        return userService.getCommonFriends(id1, id2);
    }

    @GetMapping("/{id}/feed")
    public List getUserEvents(@PathVariable("id") Long id) {
        log.info("Запрос для User: GET getUserEvents {}", id);
        return userService.getUserEvents(id);
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Запрос для User: POST add {}", user);
        return userService.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Запрос для User: PUT update {}", user);
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void makeFriends(@PathVariable("id") Long id1, @PathVariable("friendId") Long id2) {
        log.info("Запрос для User: PUT makeFriends {} {}", id1, id2);
        userService.makeFriends(id1, id2);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable("id") Long id1, @PathVariable("friendId") Long id2) {
        log.info("Запрос для User: DELETE deleteFriends {} {}", id1, id2);
        userService.deleteFriends(id1, id2);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        log.info("Запрос для User: GET getRecommendations {}", id);
        return filmService.getRecommendations(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") Long id) {
        log.info("Запрос для User: DELETE deleteUserById {}", id);
        userService.deleteUserById(id);
    }
}