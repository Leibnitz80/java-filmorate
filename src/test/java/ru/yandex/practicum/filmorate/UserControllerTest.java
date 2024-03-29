package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

@SpringBootTest
class UserControllerTest {

    @Test
    void contextLoadsUser() {
        UserService userService = new UserService(new InMemoryUserStorage());
        User user = User.builder()
                .id(1L)
                .email("sdfsdf@mail.ru")
                .login("VasyaPupkin")
                .name("name")
                .birthday(LocalDate.of(2030,1,1))
                .build();
        Assertions.assertThrows(ValidationException.class, () -> {
            userService.isValid(user);
        });
        User user2 = User.builder()
                .id(1L)
                .email("sdfsdf@mail.ru")
                .login("Vasya Pupkin")
                .name("name")
                .birthday(LocalDate.of(2020,1,10))
                .build();
        Assertions.assertThrows(ValidationException.class, () -> {
            userService.isValid(user2);
        });
    }
}