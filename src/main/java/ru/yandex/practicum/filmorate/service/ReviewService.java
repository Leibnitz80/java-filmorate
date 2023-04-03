package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
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
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public Review getReviewById(Long id) {
        reviewStorage.checkReviewContains(id);
        return reviewStorage.getReviewById(id);
    }

    public Review addReview(Review review) {
        filmStorage.checkFilmContains(review.getFilmId());
        userStorage.checkUserContains(review.getUserId());
        Review newReview = reviewStorage.addReview(review);
        userStorage.addUserEvent(newReview.getUserId(), "REVIEW", "ADD", newReview.getReviewId());
        return newReview;
    }

    public Review updateReview(Review review) {
        reviewStorage.checkReviewContains(review.getReviewId());
        filmStorage.checkFilmContains(review.getFilmId());
        userStorage.checkUserContains(review.getUserId());
        Review updateReview = reviewStorage.updateReview(review);
        userStorage.addUserEvent(updateReview.getUserId(), "REVIEW", "UPDATE", updateReview.getReviewId());
        return updateReview;
    }

    public void deleteReview(Long id) {
        Review deleteReview = reviewStorage.getReviewById(id);
        reviewStorage.checkReviewContains(id);
        userStorage.addUserEvent(deleteReview.getUserId(), "REVIEW", "REMOVE", id);
        reviewStorage.deleteReview(id);
    }

    public void addLike(Long id, Long userId) {
        final Review review = getReviewById(id);
        userStorage.checkUserContains(userId);
        review.addLike(userId);
        reviewStorage.updateReview(review);
    }

    public void addDislike(Long id, Long userId) {
        final Review review = getReviewById(id);
        userStorage.checkUserContains(userId);
        review.addDislike(userId);
        reviewStorage.updateReview(review);
    }

    public void removeLike(Long id, Long userId) {
        final Review review = getReviewById(id);
        userStorage.checkUserContains(userId);
        review.removeLike(userId);
        reviewStorage.updateReview(review);
    }

    public void removeDislike(Long id, Long userId) {
        final Review review = getReviewById(id);
        userStorage.checkUserContains(userId);
        review.removeDislike(userId);
        reviewStorage.updateReview(review);
    }
}
