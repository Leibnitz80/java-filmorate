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
        log.info("Запрос: GET by id");
        return reviewService.getReviewById(id);
    }

    @PostMapping
    public Review add(@Valid @RequestBody Review review) {
        log.info("Запрос: POST {}", review);
        review = reviewService.addReview(review);
        log.info("Запрос: POST обработан успешно");
        return review;
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Запрос: PUT update {}", review);
        review = reviewService.updateReview(review);
        log.info("Запрос: PUT update обработан успешно");
        return review;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        log.info("Запрос: DELETE by id");
        reviewService.deleteReview(id);
    }

    @GetMapping
    public List<Review> get(@RequestParam(defaultValue  = "0") Integer filmId, @RequestParam(defaultValue  = TOP_LIMIT) Integer count) {
            return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос: PUT addLike id={}, userId={}", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос: PUT addDislike id={}, userId={}", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос: DELETE removeLike id={}, userId={}", id, userId);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос: DELETE removeDislike id={}, userId={}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}
