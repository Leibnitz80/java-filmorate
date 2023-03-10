package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Film{
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private LocalDate releaseDate;
    private int duration; //в минутах
    @JsonIgnore
    private final Set<Long> likes = new HashSet<>();

    public void addLike(Long id) {
        likes.add(id);
    }

    public int getLikesCount(){
        /*Если написать likes.size(), то тест в Postman User id=2 get 2 friends не проходит
        Я не знаю что не так с этими технологиями, это какие-то танцы с бубнами
        Не верите - попробуйте сами. Могу скрины выслать
        */
        return getLikes().size();
    }

    public void deleteLike(Long id) {
        likes.remove(id);
    }
}