package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class ReviewService {

    @Qualifier("reviewDbStorage")
    private final ReviewStorage reviewStorage;
    private final FeedService feedService;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
                         FeedService feedService,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage
    ) {
        this.reviewStorage = reviewStorage;
        this.feedService = feedService;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review create(Review newReview) {
        userStorage.getUserById(newReview.getUserId());
        filmStorage.getFilmById(newReview.getFilmId());
        Review review = reviewStorage.create(newReview);

        feedService.addReviewEvent(review.getUserId(), review.getReviewId());

        return review;
    }

    public Review update(Review review) {
        userStorage.getUserById(review.getUserId());
        filmStorage.getFilmById(review.getFilmId());
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
