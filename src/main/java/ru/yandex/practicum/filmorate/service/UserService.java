package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        isValid(user);
        user = userStorage.addUser(user);
        log.info("Запрос для User: POST add {} обработан успешно", user);
        return user;
    }

    public User update(User user) {
        isValid(user);
        userStorage.updateUser(user);
        log.info("Запрос для User: PUT update {} обработан успешно", user);
        return user;
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUser(id);
        log.info("Запрос для User: DELETE deleteUserById {} обработан успешно", id);
    }

    public void makeFriends(Long userId, Long friendId) {
        userStorage.makeFriends(userId, friendId);
        userStorage.addUserEvent(userId, "FRIEND", "ADD", friendId);
        log.info("Запрос для User: PUT makeFriends {} {} обработан успешно", userId, friendId);
    }

    public void deleteFriends(Long friendId, Long userId) {
        userStorage.deleteFriends(friendId, userId);
        userStorage.addUserEvent(friendId, "FRIEND", "REMOVE", userId);
        log.info("Запрос для User: DELETE deleteFriends {} {} обработан успешно", friendId, userId);
    }

    public List getAll() {
        List<User> users = userStorage.getUsers();
        log.info("Запрос для User: GET getAll обработан успешно");
        return users;
    }

    public User getById(Long id) {
        User user = userStorage.getUserById(id);
        log.info("Запрос для User: GET getById {} обработан успешно", id);
        return user;
    }

    public List<User> getAllFriends(Long id) {
        userStorage.checkUserContains(id);
        List<User> users = userStorage.getAllFriends(id);
        log.info("Запрос для User: GET getAllFriends {} обработан успешно", id);
        return users;
    }

    public List<User> getCommonFriends(Long id1, Long id2) {
        List<User> users = userStorage.getCommonFriends(id1, id2);
        log.info("Запрос для User: GET getCommonFriends {} {} обработан успешно", id1, id2);
        return users;
    }

    public List<Event> getUserEvents(Long id) {
        userStorage.checkUserContains(id);
        List<Event> events = userStorage.getUserEvents(id);
        log.info("Запрос для User: GET getUserEvents {} обработан успешно", id);
        return events;
    }

    public void isValid(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("День рождения не должен быть в будущем");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("В логине не должно быть пробелов");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}