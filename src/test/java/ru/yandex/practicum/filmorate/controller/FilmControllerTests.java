package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

@SpringBootTest()
@AutoConfigureTestDatabase
class FilmControllerTests {

    @Autowired
    private FilmController filmController;

    @Test
    public void postEmptyFilmThrowsException() {
        Film newFilm = new Film();

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postEmptyFilmNameThrowsException() {
        Film newFilm = new Film();
        newFilm.setName("");
        newFilm.setDescription("Some description");
        newFilm.setReleaseDate(LocalDate.parse("2025-01-01"));
        newFilm.setDuration(120);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postLongFilmDescriptionThrowsException() {
        Film newFilm = new Film();
        newFilm.setName("Some film");
        newFilm.setDescription("Some description".repeat(200));
        newFilm.setReleaseDate(LocalDate.parse("2025-01-01"));
        newFilm.setDuration(120);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postOldFilmReleaseThrowsException() {
        Film newFilm = new Film();
        newFilm.setName("Some film");
        newFilm.setDescription("Some description");
        newFilm.setReleaseDate(LocalDate.parse("1700-01-01"));
        newFilm.setDuration(120);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postNegativeFilmDurationThrowsException() {
        Film newFilm = new Film();
        newFilm.setName("Some film");
        newFilm.setDescription("Some description");
        newFilm.setReleaseDate(LocalDate.parse("2025-01-01"));
        newFilm.setDuration(-5);

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postNormalFilmThrowsNoException() {
        Film newFilm = new Film();
        newFilm.setName("Some film");
        newFilm.setDescription("Some description");
        newFilm.setReleaseDate(LocalDate.parse("2025-01-01"));
        newFilm.setDuration(120);

        Assertions.assertDoesNotThrow(() -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void getPopularFilmsWithNegativeCountThrowsException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.getPopularFilms(-5, null, null);
        });
    }

    @Test
    public void getPopularFilmsWithZeroCountThrowsException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.getPopularFilms(0, null, null);
        });
    }

    @Test
    public void getPopularFilmsWithNegativeGenreIdThrowsException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.getPopularFilms(10, -1, null);
        });
    }

    @Test
    public void getPopularFilmsWithZeroGenreIdThrowsException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.getPopularFilms(10, 0, null);
        });
    }

    @Test
    public void getPopularFilmsWithYear1894ThrowsException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.getPopularFilms(10, null, 1894);
        });
    }

    @Test
    public void getPopularFilmsWithYearBefore1895ThrowsException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.getPopularFilms(10, null, 1800);
        });

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.getPopularFilms(10, null, 0);
        });

        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            filmController.getPopularFilms(10, null, -100);
        });
    }

    @Test
    public void getPopularFilmsWithYear1895DoesNotThrowException() {
        Assertions.assertDoesNotThrow(() -> {
            Collection<Film> films = filmController.getPopularFilms(10, null, 1895);
            Assertions.assertNotNull(films);
        });
    }
}
