package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long currId = 0L;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        checkUserContains(id);
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        user.setId(++currId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void updateUser(User user) {
        checkUserContains(user.getId());
        users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(Long id) {
        checkUserContains(id);
        deleteUserFromFriends(id);
        users.remove(id);
    }

    @Override
    public void makeFriends(Long friendId, Long userId) {
        User user1 = getUserById(friendId);
        User user2 = getUserById(userId);
        user1.addFriend(user2);
        user2.addFriend(user1);
    }

    @Override
    public void deleteFriends(Long friendId, Long userId) {
        User user1 = getUserById(friendId);
        User user2 = getUserById(userId);
        user1.deleteFriend(user2);
        user2.deleteFriend(user1);
    }

    @Override
    public List<User> getAllFriends(Long id) {
        return getUserById(id).getFriends();
    }

    @Override
    public List<User> getCommonFriends(Long id1, Long id2) {
        List<User> list1 = getUserById(id1).getFriends();
        List<User> list2 = getUserById(id2).getFriends();
        return list1.stream()
                .filter(list2::contains)
                .collect(Collectors.toList());
    }

    public void checkUserContains(Long id) {
        log.info("Валидация checkUserContains id={}", id);
        if (!users.containsKey(id)) {
            log.error(String.format("Пользователь c id= %d не найден!", id));
            throw new NotFoundException(
                    String.format("Пользователь c id= %d не найден!", id));
        }
    }

    // Удаление удаляемого user из друзей у других юзеров.
    private void deleteUserFromFriends(Long id) {
        User user1 = users.get(id);
        List<User> friends = user1.getFriends();

        friends.forEach(user2 -> user2.deleteFriend(user1));
    }

    @Override
    public List getUserEvents(Long userId) {
        throw new UnsupportedOperationException("method is not implement");
    }

    @Override
    public void addUserEvent(Long userId, String eventType, String operation, Long entityId) {
        throw new UnsupportedOperationException("method is not implement");
    }

    @Override
    public void deleteUserEvents(Long userId) {
        throw new UnsupportedOperationException("method is not implement");
    }
}