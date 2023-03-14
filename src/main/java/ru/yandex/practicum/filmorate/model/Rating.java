package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class Rating {
    private final int id;
    private final String name;

    public Rating(int id, String name) {
        this.id = id;
        this.name = name;
    }
}