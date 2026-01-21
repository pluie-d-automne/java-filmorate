package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FeedStorage feedStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(
            @Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
            FeedStorage feedStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage
    ) {
        this.reviewStorage = reviewStorage;
        this.feedStorage = feedStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review create(Review newReview) {
        userStorage.getUserById(newReview.getUserId());
        filmStorage.getFilmById(newReview.getFilmId());

        Review review = reviewStorage.create(newReview);

        feedStorage.addEvent(UserFeedEvent.builder()
                .userId(review.getUserId())
                .eventType(UserFeedEvent.EventType.REVIEW)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(review.getReviewId())
                .build());

        return review;
    }

    public Review update(Review review) {
        userStorage.getUserById(review.getUserId());
        filmStorage.getFilmById(review.getFilmId());

        Review updatedReview = reviewStorage.update(review);

        feedStorage.addEvent(UserFeedEvent.builder()
                .userId(updatedReview.getUserId())
                .eventType(UserFeedEvent.EventType.REVIEW)
                .operation(UserFeedEvent.OperationType.UPDATE)
                .entityId(updatedReview.getReviewId())
                .build());

        return updatedReview;
    }

    public Review delete(Long reviewId) {
        Review review = reviewStorage.delete(reviewId);

        feedStorage.addEvent(UserFeedEvent.builder()
                .userId(review.getUserId())
                .eventType(UserFeedEvent.EventType.REVIEW)
                .operation(UserFeedEvent.OperationType.REMOVE)
                .entityId(review.getReviewId())
                .build());

        return review;
    }

    public Review getReviewById(Long reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public Collection<Review> getFilmReviews(Long filmId, Integer count) {
        return reviewStorage.getFilmReviews(filmId, count);
    }

    public void putLike(Long reviewId, Long userId) {
        userStorage.getUserById(userId);
        reviewStorage.putLike(reviewId, userId);
    }

    public void putDislike(Long reviewId, Long userId) {
        userStorage.getUserById(userId);
        reviewStorage.putDislike(reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        userStorage.getUserById(userId);
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        userStorage.getUserById(userId);
        reviewStorage.deleteDislike(reviewId, userId);
    }
}
