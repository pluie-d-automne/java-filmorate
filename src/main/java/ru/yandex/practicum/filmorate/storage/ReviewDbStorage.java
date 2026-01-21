package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("reviewDbStorage")
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    private static final String FIND_REVIEW_BY_ID =
            "SELECT * FROM \"reviews\" WHERE \"review_id\" = ?;";

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
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_REVIEW, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newReview.getContent());
            ps.setBoolean(2, newReview.getIsPositive());
            ps.setLong(3, newReview.getUserId());
            ps.setLong(4, newReview.getFilmId());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Не удалось получить review_id после вставки.");
        }

        newReview.setReviewId(key.longValue());
        newReview.setUseful(0);
        return newReview;
    }

    @Override
    public Review update(Review review) {
        Optional<Review> oldReview = findOne(FIND_REVIEW_BY_ID, review.getReviewId());
        if (oldReview.isEmpty()) {
            throw new NotFoundException("Отзыв с id=" + review.getReviewId() + " не найден.");
        }

        update(UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());

        return findOne(FIND_REVIEW_BY_ID, review.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + review.getReviewId() + " пропал."));
    }

    @Override
    public Review delete(Long reviewId) {
        Review review = findOne(FIND_REVIEW_BY_ID, reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + reviewId + " не найден."));

        delete(DELETE_REVIEW, reviewId);
        return review;
    }

    @Override
    public Review getReviewById(Long reviewId) {
        return findOne(FIND_REVIEW_BY_ID, reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + reviewId + " не найден."));
    }

    @Override
    public Collection<Review> getFilmReviews(Long filmId, Integer count) {
        if (filmId == -1) {
            return findMany(FIND_ALL_REVIEWS, count);
        }
        return findMany(FIND_REVIEWS_BY_FILM, filmId, count);
    }

    @Override
    public void putLike(Long reviewId, Long userId) {
        ensureReviewExists(reviewId);

        Optional<Boolean> reaction = findReaction(reviewId, userId);

        if (reaction.isEmpty()) {
            insert(INSERT_REACTION, reviewId, userId, true);
            ensureUsefulUpdated(1, reviewId);
            return;
        }

        if (Boolean.TRUE.equals(reaction.get())) {
            return;
        }

        update(UPDATE_REACTION, true, reviewId, userId);
        ensureUsefulUpdated(2, reviewId);
    }

    @Override
    public void putDislike(Long reviewId, Long userId) {
        ensureReviewExists(reviewId);

        Optional<Boolean> reaction = findReaction(reviewId, userId);

        if (reaction.isEmpty()) {
            insert(INSERT_REACTION, reviewId, userId, false);
            ensureUsefulUpdated(-1, reviewId);
            return;
        }

        if (Boolean.FALSE.equals(reaction.get())) {
            return;
        }

        update(UPDATE_REACTION, false, reviewId, userId);
        ensureUsefulUpdated(-2, reviewId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        ensureReviewExists(reviewId);

        Optional<Boolean> reaction = findReaction(reviewId, userId);
        if (reaction.isPresent() && Boolean.TRUE.equals(reaction.get())) {
            delete(DELETE_REACTION, reviewId, userId);
            ensureUsefulUpdated(-1, reviewId);
        }
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        ensureReviewExists(reviewId);

        Optional<Boolean> reaction = findReaction(reviewId, userId);
        if (reaction.isPresent() && Boolean.FALSE.equals(reaction.get())) {
            delete(DELETE_REACTION, reviewId, userId);
            ensureUsefulUpdated(1, reviewId);
        }
    }

    private Optional<Boolean> findReaction(Long reviewId, Long userId) {
        try {
            Boolean isLike = jdbc.queryForObject(FIND_REACTION, Boolean.class, reviewId, userId);
            return Optional.ofNullable(isLike);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private void ensureReviewExists(Long reviewId) {
        if (findOne(FIND_REVIEW_BY_ID, reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден.");
        }
    }

    private void ensureUsefulUpdated(int delta, Long reviewId) {
        int updated = jdbc.update(UPDATE_USEFUL_DELTA, delta, reviewId);
        if (updated == 0) {
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден.");
        }
    }
}
