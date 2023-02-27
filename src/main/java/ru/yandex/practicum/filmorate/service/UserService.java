package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
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
        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void deleteFriends(Long id1, Long id2) {
        User user1 = userStorage.getUserById(id1);
        User user2 = userStorage.getUserById(id2);
        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public List getAll() {
        return userStorage.getUsers();
    }

    public User getById(@PathVariable("id") Long id) {
        return userStorage.getUserById(id);
    }

    public List getAllFriends(Long Id) {
        User user = userStorage.getUserById(Id);
        List<User> friendList = new ArrayList<>();
        for (Long friend : user.getAllFriends()) {
            friendList.add(userStorage.getUserById(friend));
        }
        return friendList;
    }

    public List getCommonFriends(Long id1, Long id2) {
        List<Long> set1 = userStorage.getUserById(id1).getAllFriends();
        List<Long> set2 = userStorage.getUserById(id2).getAllFriends();
        ArrayList<User> result = new ArrayList<>();
        for (int i = 0; i < set1.size(); i++) {
            Long currentId = set1.get(i);
            if (!currentId.equals(id2) && set2.contains(currentId)) {
                result.add(userStorage.getUserById(currentId));
            }
        }
        return result;
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