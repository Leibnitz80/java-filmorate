package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long currId = 0L;

    @Override
    public List getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        checkUserContains(id);
        return users.get(id);
    }

    @Override
    public void addUser(User user) {
        user.setId(++currId);
        users.put(user.getId(),user);
    }

    @Override
    public void updateUser(User user) {
        checkUserContains(user.getId());
        users.put(user.getId(),user);
    }

    @Override
    public void deleteUser(Long id) {
        checkUserContains(id);
        users.remove(id);
    }

    private void checkUserContains(Long id) {
        if (!users.containsKey(id)) {
            log.info("Валидация checkUserContains id={}", id);
            throw new NotFoundException(
                    String.format("Пользователь c id= %d не найден!", id));
        }
    }
}