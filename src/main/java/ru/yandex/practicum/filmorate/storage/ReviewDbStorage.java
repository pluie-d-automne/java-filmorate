package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import org.springframework.dao.EmptyResultDataAccessException;


import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("reviewDbStorage")
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    private static final String FIND_REVIEW_BY_ID =
            "SELECT * FROM \"reviews\" WHERE \"review_id\" = ?;";

    private static final String FIND_REVIEW_BY_FILM_AND_USER =
            "SELECT * FROM \"reviews\" WHERE \"film_id\" = ? AND \"user_id\" = ?;";

    private static final String INSERT_REVIEW =
            "INSERT INTO \"reviews\" (\"content\", \"is_positive\", \"user_id\", \"film_id\", \"useful\") " +
                    "VALUES (?, ?, ?, ?, 0);";

    private static final String UPDATE_REVIEW =
            "UPDATE \"reviews\" SET " +
                    "\"content\" = ?, " +
                    "\"is_positive\" = ? " +
                    "WHERE \"review_id\" = ?;";

    private static final String DELETE_REVIEW =
            "DELETE FROM \"reviews\" WHERE \"review_id\" = ?;";

    private static final String FIND_REVIEWS_BY_FILM =
            "SELECT * FROM \"reviews\" " +
                    "WHERE \"film_id\" = ? " +
                    "ORDER BY \"useful\" DESC " +
                    "LIMIT ?;";

    private static final String FIND_ALL_REVIEWS =
            "SELECT * FROM \"reviews\" " +
                    "ORDER BY \"useful\" DESC " +
                    "LIMIT ?;";

    private static final String INSERT_REACTION =
            "INSERT INTO \"review_reactions\" (\"review_id\", \"user_id\", \"is_like\") " +
                    "VALUES (?, ?, ?);";

    private static final String FIND_REACTION =
            "SELECT \"is_like\" FROM \"review_reactions\" WHERE \"review_id\" = ? AND \"user_id\" = ?;";

    private static final String UPDATE_REACTION =
            "UPDATE \"review_reactions\" SET \"is_like\" = ? " +
                    "WHERE \"review_id\" = ? AND \"user_id\" = ?;";

    private static final String DELETE_REACTION =
            "DELETE FROM \"review_reactions\" " +
                    "WHERE \"review_id\" = ? AND \"user_id\" = ?;";

    private static final String UPDATE_USEFUL_DELTA =
            "UPDATE \"reviews\" SET \"useful\" = \"useful\" + ? " +
                    "WHERE \"review_id\" = ?;";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review create(Review newReview) {
        insert(INSERT_REVIEW,
                newReview.getContent(),
                newReview.getIsPositive(),
                newReview.getUserId(),
                newReview.getFilmId());

        Optional<Review> createdReview = findOne(FIND_REVIEW_BY_FILM_AND_USER,
                newReview.getFilmId(), newReview.getUserId());

        if (createdReview.isPresent()) {
            Long id = createdReview.get().getReviewId();
            newReview.setReviewId(id);
        }

        return newReview;
    }

    @Override
    public Review update(Review review) {
        Optional<Review> oldReview = findOne(FIND_REVIEW_BY_ID, review.getReviewId());

        if (oldReview.isPresent()) {
            update(UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());
            Optional<Review> updatedReview = findOne(FIND_REVIEW_BY_ID, review.getReviewId());

            if (updatedReview.isPresent()) {
                return updatedReview.get();
            } else {
                throw new NotFoundException("Отзыв с id=" + review.getReviewId() + " пропал.");
            }
        } else {
            throw new NotFoundException("Отзыв с id=" + review.getReviewId() + " не найден.");
        }
    }

    @Override
    public Review delete(Long reviewId) {
        Optional<Review> review = findOne(FIND_REVIEW_BY_ID, reviewId);

        if (review.isPresent()) {
            delete(DELETE_REVIEW, reviewId);
            return review.get();
        } else {
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден.");
        }
    }

    @Override
    public Review getReviewById(Long reviewId) {
        Optional<Review> review = findOne(FIND_REVIEW_BY_ID, reviewId);

        if (review.isPresent()) {
            return review.get();
        } else {
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден.");
        }
    }

    @Override
    public Collection<Review> getFilmReviews(Long filmId, Integer count) {

        if (filmId == -1) {
            return findMany(FIND_ALL_REVIEWS, count);
        } else {
            return findMany(FIND_REVIEWS_BY_FILM, filmId, count);
        }
    }

    @Override
    public void putLike(Long reviewId, Long userId) {
        Optional<Boolean> reaction = findReaction(reviewId, userId);

        if (reaction.isEmpty()) {
            insert(INSERT_REACTION, reviewId, userId, true);
            update(UPDATE_USEFUL_DELTA, 1, reviewId);
            return;
        }

        if (reaction.get()) {
            return;
        }

        update(UPDATE_REACTION, true, reviewId, userId);
        update(UPDATE_USEFUL_DELTA, 2, reviewId);
    }

    @Override
    public void putDislike(Long reviewId, Long userId) {
        Optional<Boolean> reaction = findReaction(reviewId, userId);

        if (reaction.isEmpty()) {
            insert(INSERT_REACTION, reviewId, userId, false);
            update(UPDATE_USEFUL_DELTA, -1, reviewId);
            return;
        }

        if (!reaction.get()) {
            return;
        }

        update(UPDATE_REACTION, false, reviewId, userId);
        update(UPDATE_USEFUL_DELTA, -2, reviewId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        Optional<Boolean> reaction = findReaction(reviewId, userId);

        if (reaction.isPresent() && reaction.get()) {
            delete(DELETE_REACTION, reviewId, userId);
            update(UPDATE_USEFUL_DELTA, -1, reviewId);
        }
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        Optional<Boolean> reaction = findReaction(reviewId, userId);

        if (reaction.isPresent() && !reaction.get()) {
            delete(DELETE_REACTION, reviewId, userId);
            update(UPDATE_USEFUL_DELTA, 1, reviewId);
        }
    }


    private Optional<Boolean> findReaction(Long reviewId, Long userId) {
        try {
            Boolean isLike = jdbc.queryForObject(FIND_REACTION, Boolean.class, reviewId, userId);
            return Optional.of(isLike);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
