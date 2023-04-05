package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private static final String TOP_LIMIT = "10";
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review getById(@PathVariable("id") Long id) {
        log.info("Запрос для Review: GET getById {}", id);
        return reviewService.getReviewById(id);
    }

    @PostMapping
    public Review add(@Valid @RequestBody Review review) {
        log.info("Запрос для Review: POST add {}", review);
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Запрос для Review: PUT update {}", review);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        log.info("Запрос для Review: DELETE delete {}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping
    public List<Review> get(@RequestParam(required = false) Integer filmId, @RequestParam(defaultValue  = TOP_LIMIT) Integer count) {
        log.info("Запрос для Review: GET get {} {}", filmId, count);
        if (filmId == null)
            return reviewService.getReviews();
        else
            return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос для Review: PUT addLike {} {}", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос для Review: PUT addDislike {} {}", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос для Review: DELETE removeLike {} {}", id, userId);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос для Review: DELETE removeDislike {} {}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}
