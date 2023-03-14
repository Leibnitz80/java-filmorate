package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testPublicMethodsUser() {
        User user1 = User.builder()
                .email("sdffsdf@mail.ru")
                .login("First")
                .name("name1")
                .birthday(LocalDate.of(2015,1,1))
                .build();

        user1 = userStorage.addUser(user1);
        Long user1Id = user1.getId();

        Assertions.assertEquals("name1", user1.getName(), "Поле name должно быть name1");

        // testUpdateUser + testFindUserById
        user1.setName("updated");
        userStorage.updateUser(user1);
        user1 = userStorage.getUserById(user1.getId());

        Assertions.assertEquals("updated", user1.getName(), "Поле name должно быть updated");

        // testGetUsers
        User user2 = User.builder()
                .email("second@mail.ru")
                .login("Tsar")
                .name("Peter")
                .birthday(LocalDate.of(2014,1,1))
                .build();
        user2 = userStorage.addUser(user2);
        List<User> list = userStorage.getUsers();

        Assertions.assertEquals(2, list.size(), "Должно быть 2 пользователя");

        //testMakeFriends + testGetAllFriends
        userStorage.makeFriends(user1.getId(),user2.getId());
        list = userStorage.getAllFriends(user1.getId());
        Assertions.assertEquals(1, list.size(), "Должен быть 1 друг");
        Assertions.assertEquals(user2.getId(), list.get(0).getId(), "id друга должно быть равно " + user2.getId());
        list = userStorage.getAllFriends(user2.getId());
        Assertions.assertEquals(0, list.size(), "У второго ещё не должно быть друзей");

        //testDeleteFriends
        userStorage.deleteFriends(user1.getId(),user2.getId());
        list = userStorage.getAllFriends(user1.getId());
        Assertions.assertEquals(0, list.size(), "Не должно быть друзей");

        //testGetCommonFriends
        User user3 = User.builder()
                .email("third@mail.ru")
                .login("third")
                .name("Paul")
                .birthday(LocalDate.of(2013,1,1))
                .build();
        user3 = userStorage.addUser(user3);
        //обоюдная дружба 1-3 и 2-3 --> общий друг id=3
        userStorage.makeFriends(user1.getId(),user3.getId());
        userStorage.makeFriends(user2.getId(),user3.getId());
        userStorage.makeFriends(user3.getId(),user1.getId());
        userStorage.makeFriends(user3.getId(),user2.getId());
        list = userStorage.getCommonFriends(user1.getId(), user2.getId());
        Assertions.assertEquals(user3.getId(), list.get(0).getId(), "Должен быть один общий друг id =3");

        //testDeleteUser
        userStorage.deleteUser(user1Id);
        Assertions.assertThrows(NotFoundException.class, () -> {
            userStorage.getUserById(user1Id);
        });
    }

    @Test
    public void testPublicMethodsFilm() {
        User user1 = User.builder()
                .email("sdfsdf@mail.ru")
                .login("VasyaPupkin")
                .name("name")
                .birthday(LocalDate.of(2015, 1, 1))
                .build();

        user1 = userStorage.addUser(user1);
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(1));
        genres.add(new Genre(2));

        Film film1 = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2015, 1, 1))
                .duration(120)
                .mpa(new Mpa(1))
                .genres(genres)
                .build();

        //testAddFilm + getFilmById
        filmStorage.addFilm(film1);
        film1 = filmStorage.getFilmById(1);
        Assertions.assertEquals(1, film1.getId(), "id должен быть равен 1");

        //testUpdateFilm
        film1.setName("name updated");
        filmStorage.updateFilm(film1);
        film1 = filmStorage.getFilmById(1);
        Assertions.assertEquals("name updated", film1.getName(), "Name должен быть равен \"name updated\"");

        //testGetFilmGenres
        List<Genre> filmGenres = filmStorage.getFilmGenres(1);
        Assertions.assertEquals(2, filmGenres.size(), "У фильма должно быть 2 жанра");
        Assertions.assertEquals("Комедия", filmGenres.get(0).getName(), "Первый жанр должен быть комедия");
        Assertions.assertEquals("Драма", filmGenres.get(1).getName(), "Второй жанр должен быть драма");

        //testAddLike
        filmStorage.addLike(1,user1.getId());
        film1 = filmStorage.getFilmById(1);
        Assertions.assertEquals(1, film1.getLikesCount(), "Должен быть один лайк");
        Assertions.assertEquals(user1.getId(), film1.getLikes().get(0), "Лайк должен быть от пользователя " + user1.getId());

        //testDeleteLike
        filmStorage.deleteLike(1,user1.getId());
        film1 = filmStorage.getFilmById(1);
        Assertions.assertEquals(0, film1.getLikesCount(), "Не должно быть лайков");

        //testGetFilmById wrong number
        Assertions.assertThrows(NotFoundException.class, () -> {
            filmStorage.getFilmById(333);
        });

        //testGetFilms
        Film film2 = Film.builder()
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.of(2016, 1, 1))
                .duration(140)
                .mpa(new Mpa(2))
                .genres(genres)
                .build();
        filmStorage.addFilm(film2);
        List<Film> filmList = filmStorage.getFilms();
        Assertions.assertEquals(2, filmList.size(), "Должно быть 2 фильма");

        //testDeleteFilm
        filmStorage.deleteFilm(1);
        filmList = filmStorage.getFilms();
        Assertions.assertEquals(1, filmList.size(), "Должен быть 1 фильм");
        Assertions.assertThrows(NotFoundException.class, () -> {
            filmStorage.getFilmById(1);
        });

        //Clear
        userStorage.deleteUser(user1.getId());
    }
}