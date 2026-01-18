package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.UUID;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
public class ReviewControllerTests {

    @Autowired
    private ReviewController reviewController;

    @Autowired
    private UserController userController;

    @Autowired
    private FilmController filmController;

    private Long userId;
    private Long filmId;

    @BeforeEach
    public void setUp() {
        String suffix = UUID.randomUUID().toString();

        User user = new User();
        user.setEmail("some+" + suffix + "@email.ru");
        user.setLogin("someLogin_" + suffix);
        user.setName("someName");
        user.setBirthday(LocalDate.parse("2000-01-01"));
        User createdUser = userController.create(user);
        userId = createdUser.getId();

        Film film = new Film();
        film.setName("Some film " + suffix);
        film.setDescription("Some description");
        film.setReleaseDate(LocalDate.parse("2000-01-01"));
        film.setDuration(120);
        Film createdFilm = filmController.create(film);
        filmId = createdFilm.getId();
    }


    @Test
    public void postEmptyReviewThrowsException() {
        Review review = new Review();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.create(review);
        });
    }

    @Test
    public void postNullContentThrowsException() {
        Review review = new Review();
        review.setContent(null);
        review.setIsPositive(true);
        review.setUserId(userId);
        review.setFilmId(filmId);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.create(review);
        });
    }

    @Test
    public void postEmptyContentThrowsException() {
        Review review = new Review();
        review.setContent("");
        review.setIsPositive(true);
        review.setUserId(userId);
        review.setFilmId(filmId);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.create(review);
        });
    }

    @Test
    public void postBlankContentThrowsException() {
        Review review = new Review();
        review.setContent("   ");
        review.setIsPositive(true);
        review.setUserId(userId);
        review.setFilmId(filmId);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.create(review);
        });
    }

    @Test
    public void postNullIsPositiveThrowsException() {
        Review review = new Review();
        review.setContent("Some content");
        review.setIsPositive(null);
        review.setUserId(userId);
        review.setFilmId(filmId);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.create(review);
        });
    }

    @Test
    public void postNullUserIdThrowsException() {
        Review review = new Review();
        review.setContent("Some content");
        review.setIsPositive(true);
        review.setUserId(null);
        review.setFilmId(filmId);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.create(review);
        });
    }

    @Test
    public void postNullFilmIdThrowsException() {
        Review review = new Review();
        review.setContent("Some content");
        review.setIsPositive(true);
        review.setUserId(userId);
        review.setFilmId(null);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.create(review);
        });
    }

    @Test
    public void postValidReviewThrowsNoException() {
        Review review = new Review();
        review.setContent("Some content");
        review.setIsPositive(true);
        review.setUserId(userId);
        review.setFilmId(filmId);

        Assertions.assertDoesNotThrow(() -> {
            reviewController.create(review);
        });
    }

    @Test
    public void putEmptyReviewThrowsException() {
        Review review = new Review();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.update(review);
        });
    }

    @Test
    public void putNullReviewIdThrowsException() {
        Review review = new Review();
        review.setReviewId(null);
        review.setContent("Updated content");
        review.setIsPositive(false);
        review.setUserId(userId);
        review.setFilmId(filmId);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.update(review);
        });
    }

    @Test
    public void putEmptyContentThrowsException() {
        Review created = createValidReview();

        Review update = new Review();
        update.setReviewId(created.getReviewId());
        update.setContent("");
        update.setIsPositive(false);
        update.setUserId(userId);
        update.setFilmId(filmId);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            reviewController.update(update);
        });
    }

    @Test
    public void putValidReviewThrowsNoException() {
        Review created = createValidReview();

        Review update = new Review();
        update.setReviewId(created.getReviewId());
        update.setContent("Updated content");
        update.setIsPositive(false);
        update.setUserId(userId);
        update.setFilmId(filmId);

        Assertions.assertDoesNotThrow(() -> {
            reviewController.update(update);
        });
    }

    @Test
    public void deleteNullIdThrowsException() {
        Assertions.assertThrows(Exception.class, () -> {
            reviewController.delete(null);
        });
    }

    @Test
    public void getNullIdThrowsException() {
        Assertions.assertThrows(Exception.class, () -> {
            reviewController.getReviewById(null);
        });
    }

    private Review createValidReview() {
        Review review = new Review();
        review.setContent("Some content");
        review.setIsPositive(true);
        review.setUserId(userId);
        review.setFilmId(filmId);
        return reviewController.create(review);
    }
}
