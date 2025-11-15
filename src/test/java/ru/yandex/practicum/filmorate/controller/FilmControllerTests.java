package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@SpringBootTest()
class FilmControllerTests {

    @Autowired
    private FilmController filmController;

    @Test
    public void postEmptyFilmThrowsException() {
        Film newFilm = new Film();

        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postEmptyFilmNameThrowsException() {
        Film newFilm = new Film();
        newFilm.setName("");
        newFilm.setDescription("Some description");
        newFilm.setReleaseDate("2025-01-01");
        newFilm.setDuration(120);

        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postLongFilmDescriptionThrowsException() {
        Film newFilm = new Film();
        newFilm.setName("Some film");
        newFilm.setDescription("Some description".repeat(200));
        newFilm.setReleaseDate("2025-01-01");
        newFilm.setDuration(120);

        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postOldFilmReleaseThrowsException() {
        Film newFilm = new Film();
        newFilm.setName("Some film");
        newFilm.setDescription("Some description");
        newFilm.setReleaseDate("1700-01-01");
        newFilm.setDuration(120);

        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postNegativeFilmDurationThrowsException() {
        Film newFilm = new Film();
        newFilm.setName("Some film");
        newFilm.setDescription("Some description");
        newFilm.setReleaseDate("2025-01-01");
        newFilm.setDuration(-5);

        Assertions.assertThrows(ValidationException.class, () -> {
            filmController.create(newFilm);
        });
    }

    @Test
    public void postNormalFilmThrowsNoException() {
        Film newFilm = new Film();
        newFilm.setName("Some film");
        newFilm.setDescription("Some description");
        newFilm.setReleaseDate("2025-01-01");
        newFilm.setDuration(120);

        Assertions.assertDoesNotThrow(() -> {
            filmController.create(newFilm);
        });
    }
}
