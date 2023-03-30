package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long id);

    Review getReviewById(Long id);

    List getReviews();

    List getReviewsByFilmId(int filmId, int count);

    void checkReviewContains(Long id);
}
