package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Film {
    private int id = 0;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private LocalDate releaseDate;
    @NotBlank
    private int duration; //в минутах
}