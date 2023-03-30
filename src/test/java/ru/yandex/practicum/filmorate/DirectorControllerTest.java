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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DirectorControllerTest {
    private ResponseEntity<Director> response;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DirectorStorage directorStorage;
    private Director director1;
    private Director director2;

    @BeforeEach
    public void beforeEach() {
        director1 = new Director(null, "Котянский");
        director2 = new Director(null, "Собакевич");
    }

    @Test
    @DisplayName("Тест на добавление нового режиссера и получение ИД")
    void addNewTest() {
        response = getPostResponse(director1);
        int id = response.getBody().getId();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), directorStorage.getDirectorById(id));
    }

    @Test
    @DisplayName("Тест на обновление режиссера")
    void updateTest() {
        response = getPostResponse(director1);
        Director newDirector = response.getBody();

        newDirector.setName("Халтуркин");
        response = getPutResponse(newDirector);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getName(), "Халтуркин");
    }

    @Test
    @DisplayName("Тест на получение полного списка режиссеров")
    void getAllTest() {
        response = getPostResponse(director1);
        response = getPostResponse(director2);

        ResponseEntity<Director[]> response = restTemplate.getForEntity("/directors", Director[].class);
        List<Director> directorsList = Arrays.stream(response.getBody()).collect(Collectors.toList());
        assertEquals(directorsList, directorStorage.getDirectors());
    }

    @Test
    @DisplayName("Тест на получение режиссера по ИД")
    void getByIdTest() {
        response = getPostResponse(director1);
        int id = response.getBody().getId();

        response = restTemplate.getForEntity("/directors/" + id, Director.class);
        assertEquals(response.getBody(), directorStorage.getDirectorById(id));
    }

    @Test
    @DisplayName("Тест на удаление режиссера")
    void deleteByIdTest() {
        response = getPostResponse(director1);
        int id = response.getBody().getId();

        response = restTemplate.getForEntity("/directors/" + id, Director.class);
        assertEquals(response.getBody(), directorStorage.getDirectorById(id));

        HttpEntity<Director> entity = new HttpEntity<>(director1);
        response = restTemplate.exchange("/directors/" + id, HttpMethod.DELETE, entity, Director.class);

        response = restTemplate.getForEntity("/directors/" + id, Director.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Тест на получение режиссера по несуществующему ИД")
    void getByWrongIdTest() {
        response = restTemplate.getForEntity("/directors/12345", Director.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Director> getPostResponse(Director director) {
        return restTemplate.postForEntity("/directors", director, Director.class);
    }

    private ResponseEntity<Director> getPutResponse(Director director) {
        HttpEntity<Director> entity = new HttpEntity<>(director);
        return restTemplate.exchange("/directors", HttpMethod.PUT, entity, Director.class, director.getId());
    }
}
