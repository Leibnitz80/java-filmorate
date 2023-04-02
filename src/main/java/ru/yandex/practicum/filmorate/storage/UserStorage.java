package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
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

    List<Event> getUserEvents(Long userId);

    void addUserEvent(Long userId, String eventType, String operation, Long entityId);

    void deleteUserEvents(Long userId);

    void checkUserContains(Long userId);
}