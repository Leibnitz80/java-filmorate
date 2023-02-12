package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void contextLoadsFilm() {
		FilmController filmController = new FilmController();
		Film film = Film.builder()
				.id(1)
				.name("")
				.description("Описание фильма")
				.releaseDate(LocalDate.of(2000,1,1))
				.duration(20)
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			filmController.isValid(film);
		});
		Film film2 = Film.builder()
				.id(1)
				.name("Название фильма")
				.description("Описание фильма тестируем более 200 символов. Первый сезон сериала рассказывает о трёх женщинах, которые в разное время живут в одном и том же особняке в Пасадине (штат Калифорния) и сталкиваются с супружеской неверностью. В 1963 году Бет Энн Стэнтон наслаждается жизнью счастливой домохозяйки, пока не узнает об интрижке мужа с официанткой; в 1984 году светская львица Симона Гроув обнаруживает, что её третий муж является геем, и сама заводит роман с молодым парнем; в 2019 году адвокат Тейлор Хардинг проходит проверку отношений, когда она и её муж Илай испытывают влечение к одной и той же женщине. В каждом браке неверность становится точкой отсчета событий, которые приводят к убийствам.")
				.releaseDate(LocalDate.of(2000,1,1))
				.duration(20)
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			filmController.isValid(film2);
		});
		Film film3 = Film.builder()
				.id(1)
				.name("Название фильма")
				.description("Описание фильма")
				.releaseDate(LocalDate.of(1776,1,1))
				.duration(20)
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			filmController.isValid(film3);
		});
		Film film4 = Film.builder()
				.id(1)
				.name("Название фильма")
				.description("Описание фильма")
				.releaseDate(LocalDate.of(2010,1,1))
				.duration(0)
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			filmController.isValid(film4);
		});
	}

	@Test
	void contextLoadsUser() {
		UserController userController = new UserController();
		User user = User.builder()
				.id(1)
				.email("")
				.login("login")
				.name("name")
				.birthday(LocalDate.of(2000,1,1))
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			userController.isValid(user);
		});
		User user2 = User.builder()
				.id(1)
				.email("sdfsdf")
				.login("login")
				.name("name")
				.birthday(LocalDate.of(2000,1,1))
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			userController.isValid(user2);
		});
		User user3 = User.builder()
				.id(1)
				.email("sdfsdf")
				.login("")
				.name("name")
				.birthday(LocalDate.of(2000,1,1))
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			userController.isValid(user3);
		});
		User user4 = User.builder()
				.id(1)
				.email("sdfsdf")
				.login("Вася Пупкин")
				.name("name")
				.birthday(LocalDate.of(2000,1,1))
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			userController.isValid(user4);
		});
		User user5 = User.builder()
				.id(1)
				.email("sdfsdf")
				.login("Вася Пупкин")
				.name("name")
				.birthday(LocalDate.of(2030,1,1))
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			userController.isValid(user5);
		});
	}
}