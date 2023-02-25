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
        if (!users.containsKey(id)) {
            log.info("Запрос: GET getUserById обработан с ошибкой: Несуществующий объект");
            throw new NotFoundException(
                    String.format("Пользователь c id= %d не найден!", id));
        }
        log.info("Запрос: GET getUserById обработан успешно");
        return users.get(id);
    }

    @Override
    public void addUser(User user) {
        user.setId(++currId);
        users.put(user.getId(),user);
        log.info("Запрос: POST обработан успешно");
    }

    @Override
    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Запрос: PUT обработан с ошибкой: Несуществующий объект");
            throw new NotFoundException("Несуществующий объект");
        }
        users.put(user.getId(),user);
        log.info("Запрос: PUT обработан успешно");
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            log.info("Запрос: DELETE deleteUser обработан с ошибкой: Несуществующий объект");
            throw new NotFoundException(
                    String.format("Пользователь c id= %d не найден!", id));
        }
        users.remove(id);
    }
}