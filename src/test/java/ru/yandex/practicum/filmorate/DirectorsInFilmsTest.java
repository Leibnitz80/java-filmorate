package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DirectorsInFilmsTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private final FilmDbStorage filmDbStorage;
    private final DirectorStorage directorStorage;
    private ResponseEntity<Film> response;
    private Film film1;
    private Film film2;
    private User user1;
    private Director director;

    @BeforeEach
    public void beforeEach() {
        film1 = new Film();
        film1.setName("cats-killers");
        film1.setDescription("description of horror cats");
        film1.setReleaseDate(LocalDate.of(1950, 11, 11));
        film1.setDuration(60);
        film1.setMpa(new Mpa(1, "G"));

        film2 = new Film();
        film2.setName("cats-savers");
        film2.setDescription("description of holy cats");
        film2.setReleaseDate(LocalDate.of(2001, 11, 11));
        film2.setDuration(42);
        film2.setMpa(new Mpa(1, "G"));

        director = new Director(1, "Косяковский");
        ResponseEntity<Director> directorResponse =
                restTemplate.postForEntity("/directors", director, Director.class);

        user1 = new User(null, "ivanov@gmail.com", "ivanov", "Ivan", LocalDate.of(1988, 10, 11));
        ResponseEntity<User> response = restTemplate.postForEntity("/users", user1, User.class);
        user1.setId(response.getBody().getId());
    }

    @Test
    @DisplayName("Тест на добавление фильма с режиссером")
    void addFilmWithDirectorTest() {
        film1.setDirectors(List.of(director));
        response = getPostResponse(film1);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getDirectors().get(0),
                filmDbStorage.getFilmById(response.getBody().getId()).getDirectors().get(0));
    }

    @Test
    @DisplayName("Тест на получение фильма с режиссером")
    void getFilmWithDirectorTest() {
        film1.setDirectors(List.of(director));
        int id = getPostResponse(film1).getBody().getId();

        response = restTemplate.getForEntity("/films/" + id, Film.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getDirectors().get(0), film1.getDirectors().get(0));
    }

    @Test
    @DisplayName("Тест на обновление режиссера в фильме")
    void updateDirectorInFilmTest() {
        film1.setDirectors(List.of(director));
        Film updatedFilm = getPostResponse(film1).getBody();

        response = restTemplate.getForEntity("/films/" + updatedFilm.getId(), Film.class);

        updatedFilm.getDirectors().get(0).setName("НЕКОсяковский");
        response = getPutResponse(updatedFilm);
        Film newUpdatedFilm = response.getBody();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(newUpdatedFilm.getDirectors().get(0).getName(),
                filmDbStorage.getFilmById(newUpdatedFilm.getId()).getDirectors().get(0).getName());
    }

    @Test
    @DisplayName("Тест на получение фильмов с режиссером")
    void getAllFilmsWithDirectorTest() {
        film1.setDirectors(List.of(director));
        Film newFilm1 = getPostResponse(film1).getBody();

        film2.setDirectors(List.of(director));
        Film newFilm2 = getPostResponse(film2).getBody();

        ResponseEntity<Film[]> responseList = restTemplate.getForEntity("/films/", Film[].class);
        Film[] films = responseList.getBody();
        assertEquals(responseList.getStatusCode(), HttpStatus.OK);

        List<Film> filmsInDb = filmDbStorage.getFilms();
        for (int i = 0; i < filmsInDb.size(); i++) {
            assertEquals(films[i].getDirectors().get(0).getName(), filmsInDb.get(i).getDirectors().get(0).getName());
        }
    }

    @Test
    @DisplayName("Тест на получение фильмов конкретного режиссера и сортировкой по году выпуска")
    void getFilmsByDirectorYearSortTest() {
        film2.setDirectors(List.of(director));
        Film newFilm2 = getPostResponse(film2).getBody();

        film1.setDirectors(List.of(director));
        Film newFilm1 = getPostResponse(film1).getBody();

        ResponseEntity<Film[]> responseList = restTemplate.getForEntity("/films/director/1?sortBy=year", Film[].class);
        Film[] films = responseList.getBody();
        assertEquals(responseList.getStatusCode(), HttpStatus.OK);

        Film[] filmsInRightOrder = new Film[]{newFilm1, newFilm2};

        for (int i = 0; i < films.length; i++) {
            assertEquals(films[i].getId(), filmsInRightOrder[i].getId());
            assertEquals(films[i].getName(), filmsInRightOrder[i].getName());
            assertEquals(films[i].getDirectors().get(0).getId(), filmsInRightOrder[i].getDirectors().get(0).getId());
        }
    }

    @Test
    @DisplayName("Тест на получение фильмов конкретного режиссера и сортировкой по лайкам")
    void getFilmsByDirectorLikesSortTest() {
        film1.setDirectors(List.of(director));
        Film newFilm1 = getPostResponse(film1).getBody();

        film2.setDirectors(List.of(director));
        Film newFilm2 = getPostResponse(film2).getBody();

        HttpEntity<Film> entity = new HttpEntity<>(newFilm2);
        response = restTemplate.exchange("/films/" + newFilm2.getId() + "/like/" + user1.getId(), HttpMethod.PUT, entity, Film.class);

        ResponseEntity<Film[]> responseList = restTemplate.getForEntity("/films/director/1?sortBy=likes", Film[].class);
        Film[] films = responseList.getBody();
        assertEquals(responseList.getStatusCode(), HttpStatus.OK);

        Film[] filmsInRightOrder = new Film[]{newFilm1, newFilm2};


        for (int i = 0; i < films.length; i++) {
            assertEquals(films[i].getId(), filmsInRightOrder[i].getId());
            assertEquals(films[i].getName(), filmsInRightOrder[i].getName());
            assertEquals(films[i].getDirectors().get(0).getId(), filmsInRightOrder[i].getDirectors().get(0).getId());
        }
    }

    private ResponseEntity<Film> getPostResponse(Film film) {
        return restTemplate.postForEntity("/films", film, Film.class);
    }

    private ResponseEntity<Film> getPutResponse(Film film) {
        HttpEntity<Film> entity = new HttpEntity<>(film);
        return restTemplate.exchange("/films", HttpMethod.PUT, entity, Film.class, film.getId());
    }
}
