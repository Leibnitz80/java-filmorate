package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

@SpringBootTest
class UserControllerTest {

    @Test
    void contextLoadsUser() {
        UserController userController = new UserController();
        User user = User.builder()
                .id(1)
                .email("sdfsdf@mail.ru")
                .login("VasyaPupkin")
                .name("name")
                .birthday(LocalDate.of(2030,1,1))
                .build();
        Assertions.assertThrows(ValidationException.class, () -> {
        userController.isValid(user);
        });
        User user2 = User.builder()
                .id(1)
                .email("sdfsdf@mail.ru")
                .login("Vasya Pupkin")
                .name("name")
                .birthday(LocalDate.of(2020,1,10))
                .build();
        Assertions.assertThrows(ValidationException.class, () -> {
        userController.isValid(user2);
        });
    }
}