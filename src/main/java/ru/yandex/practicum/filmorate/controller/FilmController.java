package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_CINEMA_DATE = LocalDate.parse("1895-12-28", DateTimeFormatter.ISO_DATE);
    private final Map<Long, Film> films = new HashMap<>();

    private long getNextId() {
        long lastId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++lastId;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.trace("Возвращен список всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {
        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            String message = "Название фильма не может быть пустым.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (newFilm.getDescription() != null && newFilm.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            String message = "Максимальная длина описания — " + MAX_FILM_DESCRIPTION_LENGTH + " символов.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (newFilm.getReleaseDate() != null) {
            if (LocalDate.parse(newFilm.getReleaseDate(), DateTimeFormatter.ISO_DATE).isBefore(MIN_CINEMA_DATE)) {
                String message = "Дата релиза не может быть раньше " + MIN_CINEMA_DATE + ".";
                log.error(message);
                throw new ValidationException(message);
            }
        }
        if (newFilm.getDuration() != null && newFilm.getDuration() <= 0) {
            String message = "Продолжительность фильма должна быть положительным числом.";
            log.error(message);
            throw new ValidationException(message);
        }
        newFilm.setId(getNextId());
        log.info("Фильму присвоен id={}", newFilm.getId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм добавлен.");
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        long filmId = newFilm.getId();
        if (!films.containsKey(filmId)) {
            String message = "Фильма с id=" + filmId + " не существует.";
            log.error(message);
            throw new NotFoundException(message);
        }
        Film oldFilm = films.get(filmId);
        if (newFilm.getName() != null && !newFilm.getName().isBlank() && !newFilm.getName().equals(oldFilm.getName())) {
            oldFilm.setName(newFilm.getName());
            log.info("Название фильма изменено на {}", newFilm.getName());
        }
        if (newFilm.getDescription() != null && newFilm.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            String message = "Максимальная длина описания — " + MAX_FILM_DESCRIPTION_LENGTH + " символов.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank() && !newFilm.getDescription().equals(oldFilm.getDescription())) {
            oldFilm.setDescription(newFilm.getDescription());
            log.info("Описание фильма изменено");
        }
        if (newFilm.getReleaseDate() != null) {
            if (LocalDate.parse(newFilm.getReleaseDate(), DateTimeFormatter.ISO_DATE).isBefore(MIN_CINEMA_DATE)) {
                String message = "Дата релиза не может быть раньше " + MIN_CINEMA_DATE + ".";
                log.error(message);
                throw new ValidationException(message);
            }
        }
        if (newFilm.getReleaseDate() != null && !newFilm.getReleaseDate().equals(oldFilm.getReleaseDate())) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.info("Дата релиза изменена на {}", newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null && newFilm.getDuration() <= 0) {
            String message = "Продолжительность фильма должна быть положительным числом.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (newFilm.getDuration() != null && !newFilm.getDuration().equals(oldFilm.getDuration())) {
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Продолжительность фильма изменена на " + newFilm.getDuration());
        }
        log.info("Фильм обновлён");
        return oldFilm;
    }
}
