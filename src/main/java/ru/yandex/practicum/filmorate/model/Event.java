package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Event {
    private Long id;
    @NotBlank
    private Long timestamp;
    @NotBlank
    private Long userId;
    @NotBlank
    private String eventType;
    @NotBlank
    private String operation;
    @NotBlank
    private Long entity_id;
}