package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @EqualsAndHashCode.Exclude
    private List<Director> directors = new ArrayList<>();

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

    public Film(int id, String name,String description, LocalDate releaseDate,int duration,Mpa mpa, List genres, List likes, List<Director> directors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
        this.likes = likes;
        this.directors = directors;
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