package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getDirectors() {
        log.info("Запрос для Director: GET getDirectors обработан успешно");
        return directorStorage.getDirectors();
    }

    public Director addDirector(Director director) {
        log.info("Запрос для Director: POST addDirector {} обработан успешно", director);
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        log.info("Запрос для Director: PUT {} updateDirector обработан успешно", director);
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(Integer id) {
        log.info("Запрос для Director: DELETE {} deleteDirector обработан успешно", id);
        directorStorage.deleteDirector(id);
    }

    public Director getDirectorById(Integer id) {
        log.info("Запрос для Director: GET getDirectorById {} обработан успешно", id);
        return directorStorage.getDirectorById(id);
    }
}
