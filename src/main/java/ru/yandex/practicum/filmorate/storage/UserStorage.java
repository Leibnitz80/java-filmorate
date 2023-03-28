package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List getUsers();

    User addUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);

    User getUserById(Long id);

    void makeFriends(Long friendId, Long userId);

    void deleteFriends(Long friendId, Long userId);

    List<User> getAllFriends(Long id);

    List<User> getCommonFriends(Long id1, Long id2);

    void checkUserContains(Long userId);
}