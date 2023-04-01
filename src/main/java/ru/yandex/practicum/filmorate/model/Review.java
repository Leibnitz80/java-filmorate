package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    private Long reviewId = 0L;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Integer filmId;
    private int useful;
    @JsonIgnore
    private final Set<Long> likes = new HashSet<>();
    @JsonIgnore
    private final Set<Long> dislikes = new HashSet<>();

    public void addLike(Long userId) {
        likes.add(userId);
        updateUseful();
    }

    public void addDislike(Long userId) {
        dislikes.add(userId);
        updateUseful();
    }

    public void removeLike(Long userId) {
        likes.remove(userId);
        updateUseful();
    }

    public void removeDislike(Long userId) {
        dislikes.remove(userId);
        updateUseful();
    }

    public int getUseful() {
        updateUseful();
        return useful;
    }

    private void updateUseful() {
        useful = likes.size() - dislikes.size();
    }
}
