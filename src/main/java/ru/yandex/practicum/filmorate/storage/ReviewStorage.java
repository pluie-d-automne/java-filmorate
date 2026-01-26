package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {
    Review create(Review newReview);

    Review update(Review newReview);

    Review delete(Long reviewId);

    Review getReviewById(Long reviewId);

    Collection<Review> getFilmReviews(Long filmId, Integer count);

    void putLike(Long reviewId, Long userId);

    void putDislike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);
}
