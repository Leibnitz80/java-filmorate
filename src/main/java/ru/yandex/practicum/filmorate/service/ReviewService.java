package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.ActionType;
import ru.yandex.practicum.filmorate.model.enums.ObjectType;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ReviewService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;

    public List<Review> getReviews() {
        return reviewStorage.getReviews();
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        if (filmId == 0) {
            return getReviews();
        } else {
            List<Review> reviews = reviewStorage.getReviewsByFilmId(filmId, count);
            log.info("Запрос для Review: GET get {} {} обработан успешно", filmId, count);
            return reviews;
        }
    }

    public Review getReviewById(Long id) {
        reviewStorage.checkReviewContains(id);
        Review review = reviewStorage.getReviewById(id);
        log.info("Запрос для Review: GET getById {} обработан успешно", id);
        return review;
    }

    public Review addReview(Review review) {
        filmStorage.checkFilmContains(review.getFilmId());
        userStorage.checkUserContains(review.getUserId());
        Review newReview = reviewStorage.addReview(review);
        userStorage.addUserEvent(newReview.getUserId(), ObjectType.REVIEW.name(), ActionType.ADD.name(), newReview.getReviewId());
        log.info("Запрос для Review: POST add {} обработан успешно", review);
        return newReview;
    }

    public Review updateReview(Review review) {
        reviewStorage.checkReviewContains(review.getReviewId());
        filmStorage.checkFilmContains(review.getFilmId());
        userStorage.checkUserContains(review.getUserId());
        Review updateReview = reviewStorage.updateReview(review);
        userStorage.addUserEvent(updateReview.getUserId(), ObjectType.REVIEW.name(), ActionType.UPDATE.name(), updateReview.getReviewId());
        log.info("Запрос для Review: PUT update {} обработан успешно", review);
        return updateReview;
    }

    public void deleteReview(Long id) {
        reviewStorage.checkReviewContains(id);
        Review deleteReview = reviewStorage.getReviewById(id);
        userStorage.addUserEvent(deleteReview.getUserId(), ObjectType.REVIEW.name(), ActionType.REMOVE.name(), id);
        reviewStorage.deleteReview(id);
        log.info("Запрос для Review: DELETE delete {} обработан успешно", id);
    }

    public void addLike(Long id, Long userId) {
        userStorage.checkUserContains(userId);
        final Review review = getReviewById(id);
        review.addLike(userId);
        reviewStorage.updateReview(review);
        log.info("Запрос для Review: PUT addLike {} {} обработан успешно", id, userId);
    }

    public void addDislike(Long id, Long userId) {
        userStorage.checkUserContains(userId);
        final Review review = getReviewById(id);
        review.addDislike(userId);
        reviewStorage.updateReview(review);
        log.info("Запрос для Review: PUT addDislike {} {} обработан успешно", id, userId);
    }

    public void removeLike(Long id, Long userId) {
        userStorage.checkUserContains(userId);
        final Review review = getReviewById(id);
        review.removeLike(userId);
        reviewStorage.updateReview(review);
        log.info("Запрос для Review: DELETE removeLike {} {} обработан успешно", id, userId);
    }

    public void removeDislike(Long id, Long userId) {
        userStorage.checkUserContains(userId);
        final Review review = getReviewById(id);
        review.removeDislike(userId);
        reviewStorage.updateReview(review);
        log.info("Запрос для Review: DELETE removeDislike {} {} обработан успешно", id, userId);
    }
}
