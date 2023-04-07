package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.ActionType;
import ru.yandex.practicum.filmorate.model.enums.ObjectType;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Event {
    private Long eventId;
    @NotBlank
    private Long timestamp;
    @NotBlank
    private Long userId;
    @NotBlank
    private ObjectType eventType;
    @NotBlank
    private ActionType operation;
    @NotBlank
    private Long entityId;
}