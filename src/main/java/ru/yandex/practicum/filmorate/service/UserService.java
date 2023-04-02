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
        return userStorage.addUser(user);
    }

    public User update(User user) {
        isValid(user);
        userStorage.updateUser(user);
        return user;
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUser(id);
        userStorage.deleteUserEvents(id);
    }

    public void makeFriends(Long userId, Long friendId) {
        userStorage.makeFriends(userId, friendId);
        userStorage.addUserEvent(userId, "FRIEND", "ADD", friendId);
    }

    public void deleteFriends(Long friendId, Long userId) {
        userStorage.deleteFriends(friendId, userId);
        userStorage.addUserEvent(friendId, "FRIEND", "REMOVE", userId);
    }

    public List getAll() {
        return userStorage.getUsers();
    }

    public User getById(Long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getAllFriends(Long id) {
        userStorage.checkUserContains(id);
        return userStorage.getAllFriends(id);
    }

    public List getCommonFriends(Long id1, Long id2) {
        return userStorage.getCommonFriends(id1, id2);
    }

    public List<Event> getUserEvents(Long id) {
        userStorage.checkUserContains(id);
        return userStorage.getUserEvents(id);
    }

    public void isValid(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: День рождения не должен быть в будущем");
            throw new ValidationException("День рождения не должен быть в будущем");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: В логине не должно быть пробелов");
            throw new ValidationException("В логине не должно быть пробелов");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}