package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Mpa {
    private int id;
    private String name;

    public Mpa() {
    }

    public Mpa(int id) {
        this.id = id;
    }

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }
}