package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        userStorage.addUser(user);
        return user;
    }

    public User update(User user) {
        isValid(user);
        userStorage.updateUser(user);
        return user;
    }

    public void makeFriends(Long id1, Long id2) {
        User user1 = userStorage.getUserById(id1);
        User user2 = userStorage.getUserById(id2);
        user1.addFriend(user2);
        user2.addFriend(user1);
    }

    public void deleteFriends(Long id1, Long id2) {
        User user1 = userStorage.getUserById(id1);
        User user2 = userStorage.getUserById(id2);
        user1.deleteFriend(user2);
        user2.deleteFriend(user1);
    }

    public List getAll() {
        return userStorage.getUsers();
    }

    public User getById(@PathVariable("id") Long id) {
        return userStorage.getUserById(id);
    }

    public Set getAllFriends(Long Id) {
        return userStorage.getUserById(Id).getFriends();
    }

    public Set getCommonFriends(Long id1, Long id2) {
        System.out.println("Зашли");
        Set<User> set1 = userStorage.getUserById(id1).getFriends();
        Set<User> set2 = userStorage.getUserById(id2).getFriends();
        Set<User> common = set1.stream()
                .filter(set2::contains)
                .collect(Collectors.toSet());
        return common;
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