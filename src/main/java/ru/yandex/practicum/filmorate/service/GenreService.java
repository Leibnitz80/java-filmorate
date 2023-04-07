package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List getAll() {
        List<Genre> genres = genreStorage.getGenres();
        log.info("Запрос для Genre: GET getAll обработан успешно");
        return genres;
    }

    public Genre getById(Integer id) {
        Genre genre = genreStorage.getGenreById(id);
        log.info("Запрос: GET genres getById {} обработан успешно", id);
        return genre;
    }
}