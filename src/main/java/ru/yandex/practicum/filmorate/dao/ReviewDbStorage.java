package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
@Primary
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review addReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Reviews")
                .usingGeneratedKeyColumns("review_id");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive())
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId())
                .addValue("useful", review.getUseful());

        Long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        review.setReviewId(id);

        return getReviewById(review.getReviewId());
    }

    @Override
    public Review updateReview(Review review) {
        final String updateReviewSql = "update Reviews " +
                "set content = ?, " +
                "    isPositive = ?, " +
                "    useful = ? " +
                "where review_id = ?;";
        jdbcTemplate.update(
                updateReviewSql,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId()
        );
        updateReviewLikesDislikes(review);
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(Long id) {
        deleteReviewLikesDislikes(id);
        final String deleteReviewSql = "delete from Reviews where review_id = ?;";
        jdbcTemplate.update(deleteReviewSql, id);
    }

    @Override
    public Review getReviewById(Long id) {
        final String getReviewByIdSql =
                "select review_id, content, isPositive, user_id, film_id, useful " +
                "from Reviews where review_id = ?;";
        return jdbcTemplate.queryForObject(getReviewByIdSql, (rs, rowNum) -> makeReview(rs), id);
    }

    @Override
    public List<Review> getReviews() {
        final String getReviewsSql =
                "select review_id, content, isPositive, user_id, film_id, useful " +
                "from Reviews " +
                "order by useful desc;";
        List<Review> reviews = jdbcTemplate.query(getReviewsSql, (rs, rowNum) -> makeReview(rs));
        return Collections.unmodifiableList(reviews);
    }

    @Override
    public List getReviewsByFilmId(int filmId, int count) {
        final String getReviewsByFilmIdSql =
                "select review_id, content, isPositive, user_id, film_id, useful " +
                "from Reviews " +
                "where film_id = ? " +
                "order by useful desc, review_id " +
                "limit " + count;
        List<Review> reviews = jdbcTemplate.query(getReviewsByFilmIdSql, (rs, rowNum) -> makeReview(rs), filmId);
        return Collections.unmodifiableList(reviews);
    }

    @Override
    public void checkReviewContains(Long id) {
        String sql = "select count(1) as row_count from Reviews where review_id = ?;";
        Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("row_count"), id))
                .filter(x -> x > 0)
                .orElseThrow(() -> new NotFoundException(String.format("Отзыв c id= %d не найден!", id)));
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        Long id = rs.getLong("review_id");
        String content = rs.getString("content");
        boolean isPositive = rs.getBoolean("isPositive");
        Long userId = rs.getLong("user_id");
        int filmId = rs.getInt("film_id");
        int useful = rs.getInt("useful");

        Review review = Review.builder()
                .reviewId(id)
                .content(content)
                .isPositive(isPositive)
                .userId(userId)
                .filmId(filmId)
                .useful(useful)
                .build();
        review.getLikes().addAll(getReviewLikes(id));
        review.getDislikes().addAll(getReviewDislikes(id));
        return review;
    }

    private void deleteReviewLikesDislikes(Long reviewId) {
        jdbcTemplate.update(
                "delete from ReviewLikes where review_id = ?;",
                reviewId
        );

        jdbcTemplate.update(
                "delete from ReviewDislikes where review_id = ?;",
                reviewId
        );
    }

    private void updateReviewLikesDislikes(Review review) {
        deleteReviewLikesDislikes(review.getReviewId());

        Set<Long> likes = review.getLikes();
        if (!likes.isEmpty()) {
            batchUpdateReviewLikesDislikes(
                    "insert into ReviewLikes(review_id, user_id) values(?, ?);",
                    review.getReviewId(),
                    likes
            );
        }

        Set<Long> dislikes = review.getDislikes();
        if (!dislikes.isEmpty()) {
            batchUpdateReviewLikesDislikes(
                    "insert into ReviewDislikes(review_id, user_id) values(?, ?);",
                    review.getReviewId(),
                    dislikes
            );
        }
    }

    private List<Long> getReviewLikes(Long reviewId) {
        List<Long> likes = jdbcTemplate.query(
            "select user_id from ReviewLikes where review_id = ?;",
                (rs, rowNum) -> rs.getLong("user_id"),
                reviewId
        );
        return likes;
    }

    private List<Long> getReviewDislikes(Long reviewId) {
        List<Long> dislikes = jdbcTemplate.query(
                "select user_id from ReviewDislikes where review_id = ?;",
                (rs, rowNum) -> rs.getLong("user_id"),
                reviewId
        );
        return dislikes;
    }

    private void batchUpdateReviewLikesDislikes(String updateSql, Long reviewId, Set<Long> userIds) {
        jdbcTemplate.batchUpdate(
                updateSql,
                userIds,
                100,
                (PreparedStatement ps, Long userId) -> {
                    ps.setLong(1, reviewId);
                    ps.setLong(2, userId);
                }
        );
    }
}
