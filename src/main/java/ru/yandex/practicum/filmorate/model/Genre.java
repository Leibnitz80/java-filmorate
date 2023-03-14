package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class Genre {
    private final int id;
    private String name;

    public Genre(int id) {
        this.id = id;
    }

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}