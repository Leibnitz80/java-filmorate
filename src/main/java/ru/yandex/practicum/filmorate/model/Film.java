package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();
    @JsonIgnore
    private List<Long> likes = new ArrayList<>();

    public Film() {
    }

    public Film(String name,String description, LocalDate releaseDate,int duration,Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(int id, String name,String description, LocalDate releaseDate,int duration,Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(int id, String name,String description, LocalDate releaseDate,int duration,Mpa mpa, List genres, List likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
        this.likes = likes;
    }

    public void addLike(Long id) {
        likes.add(id);
    }

    public int getLikesCount(){
        return likes.size();
    }

    public void deleteLike(Long id) {
        likes.remove(id);
    }
}