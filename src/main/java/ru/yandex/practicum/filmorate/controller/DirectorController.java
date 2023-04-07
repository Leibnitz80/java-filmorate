package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("{id}")
    public Director getDirectorById(@PathVariable Integer id) {
        log.info("Запрос для Director: GET getDirectorById {}", id);
        return directorService.getDirectorById(id);
    }

    @DeleteMapping("{id}")
    public void deleteDirector(@PathVariable Integer id) {
        log.info("Запрос для Director: DELETE deleteDirector {} ", id);
        directorService.deleteDirector(id);
    }

    @GetMapping()
    public List<Director> getDirectors() {
        log.info("Запрос для Director: GET getDirectors");
        return directorService.getDirectors();
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("Запрос для Director: POST addDirector {}", director);
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Запрос для Director: PUT updateDirector {}", director);
        return directorService.updateDirector(director);
    }
}
