package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

@SpringBootTest
@RequiredArgsConstructor
class FilmControllerTest {
	private JdbcTemplate jdbcTemplate;
	@Test
	void contextLoadsFilm() {
		FilmService filmService = new FilmService(new InMemoryFilmStorage(),
				new InMemoryUserStorage(),
				new UserService(new UserDbStorage(jdbcTemplate)));
		Film film = Film.builder()
				.id(1)
				.name("Название фильма")
				.description("Описание фильма тестируем более 200 символов. Первый сезон сериала рассказывает о трёх женщинах, которые в разное время живут в одном и том же особняке в Пасадине (штат Калифорния) и сталкиваются с супружеской неверностью. В 1963 году Бет Энн Стэнтон наслаждается жизнью счастливой домохозяйки, пока не узнает об интрижке мужа с официанткой; в 1984 году светская львица Симона Гроув обнаруживает, что её третий муж является геем, и сама заводит роман с молодым парнем; в 2019 году адвокат Тейлор Хардинг проходит проверку отношений, когда она и её муж Илай испытывают влечение к одной и той же женщине. В каждом браке неверность становится точкой отсчета событий, которые приводят к убийствам.")
				.releaseDate(LocalDate.of(2000,1,1))
				.duration(20)
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			filmService.isValid(film);
		});
		Film film3 = Film.builder()
				.id(1)
				.name("Название фильма")
				.description("Описание фильма")
				.releaseDate(LocalDate.of(1776,1,1))
				.duration(20)
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			filmService.isValid(film3);
		});
		Film film4 = Film.builder()
				.id(1)
				.name("Название фильма")
				.description("Описание фильма")
				.releaseDate(LocalDate.of(2010,1,1))
				.duration(0)
				.build();
		Assertions.assertThrows(ValidationException.class, () -> {
			filmService.isValid(film4);
		});
	}
}