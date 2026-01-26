package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RequiredArgsConstructor
@Validated
@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public Review create(@Valid @RequestBody Review review) {
        log.info("Создается отзыв: {}", review);
        return reviewService.create(review);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Review update(@Valid @RequestBody Review review) {
        log.info("Обновляется отзыв: {}", review);
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public Review delete(@PathVariable("id") Long reviewId) {
        log.info("Удаляется отзыв с id={}", reviewId);
        return reviewService.delete(reviewId);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Long reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public Collection<Review> getReviews(@RequestParam(defaultValue = "-1") long filmId,
                                         @RequestParam(defaultValue = "10") int count) {
        return reviewService.getFilmReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable("id") Long reviewId,
                        @PathVariable Long userId) {
        log.info("Пользователь с id={} поставил лайк отзыву с id={}", userId, reviewId);
        reviewService.putLike(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislike(@PathVariable("id") Long reviewId,
                           @PathVariable Long userId) {
        log.info("Пользователь с id={} поставил дизлайк отзыву с id={}", userId, reviewId);
        reviewService.putDislike(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long reviewId,
                           @PathVariable Long userId) {
        log.info("Пользователь с id={} удалил лайк у отзыва с id={}", userId, reviewId);
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Long reviewId,
                              @PathVariable Long userId) {
        log.info("Пользователь с id={} удалил дизлайк у отзыва с id={}", userId, reviewId);
        reviewService.deleteDislike(reviewId, userId);
    }
}
