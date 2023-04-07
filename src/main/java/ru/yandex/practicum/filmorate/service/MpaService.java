package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List getAll() {
        List<Mpa> mpas = mpaStorage.getMpas();
        log.info("Запрос для Mpa: GET getAll обработан успешно");
        return mpas;
    }

    public Mpa getById(Integer id) {
        Mpa mpa = mpaStorage.getMpaById(id);
        log.info("Запрос для Mpa: GET getById {} обработан успешно", id);
        return mpa;
    }
}