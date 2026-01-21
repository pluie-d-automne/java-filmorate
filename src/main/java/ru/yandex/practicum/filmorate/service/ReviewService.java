package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;

@Service
public class ReviewService {

    @Qualifier("reviewDbStorage")
    private final ReviewStorage reviewStorage;

    private final FeedStorage feedStorage;

    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage, FeedStorage feedStorage) {
        this.reviewStorage = reviewStorage;
        this.feedStorage = feedStorage;
    }

    public Review create(Review newReview) {
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
        Review updatedReview = reviewStorage.update(review);

        feedStorage.addEvent(UserFeedEvent.builder()
                .userId(review.getUserId())
                .eventType(UserFeedEvent.EventType.REVIEW)
                .operation(UserFeedEvent.OperationType.UPDATE)
                .entityId(review.getReviewId())
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
        reviewStorage.putLike(reviewId, userId);
    }

    public void putDislike(Long reviewId, Long userId) {
        reviewStorage.putDislike(reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        reviewStorage.deleteDislike(reviewId, userId);
    }
}
