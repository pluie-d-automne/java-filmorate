package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class ReviewService {

    @Qualifier("reviewDbStorage")
    private final ReviewStorage reviewStorage;

    private final FeedService feedService;
    private final UserService userService;
    private final FilmService filmService;

    public Review create(Review newReview) {
        userService.getUserById(newReview.getUserId());
        filmService.getFilmById(newReview.getFilmId());
        Review review = reviewStorage.create(newReview);

        feedService.addReviewEvent(review.getUserId(), review.getReviewId());

        return review;
    }

    public Review update(Review review) {
        userService.getUserById(review.getUserId());
        filmService.getFilmById(review.getFilmId());
        Review updatedReview = reviewStorage.update(review);

        feedService.updateReviewEvent(updatedReview.getUserId(), updatedReview.getReviewId());

        return updatedReview;
    }

    public Review delete(Long reviewId) {
        Review review = reviewStorage.delete(reviewId);

        feedService.removeReviewEvent(review.getUserId(), review.getReviewId());

        return review;
    }

    public Review getReviewById(Long reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public Collection<Review> getFilmReviews(Long filmId, Integer count) {
        return reviewStorage.getFilmReviews(filmId, count);
    }

    public void putLike(Long reviewId, Long userId) {
        userService.getUserById(userId);
        reviewStorage.putLike(reviewId, userId);
    }

    public void putDislike(Long reviewId, Long userId) {
        userService.getUserById(userId);
        reviewStorage.putDislike(reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        userService.getUserById(userId);
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        userService.getUserById(userId);
        reviewStorage.deleteDislike(reviewId, userId);
    }
}
