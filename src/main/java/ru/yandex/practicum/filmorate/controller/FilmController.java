package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.trace("Возвращен список всех фильмов");
        return films.values();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public Film create(@Valid @RequestBody Film newFilm) {
        newFilm.setId(getNextId());
        log.info("Фильму присвоен id={}", newFilm.getId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм добавлен.");
        return newFilm;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
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
        if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank() && !newFilm.getDescription().equals(oldFilm.getDescription())) {
            oldFilm.setDescription(newFilm.getDescription());
            log.info("Описание фильма изменено");
        }
        if (newFilm.getReleaseDate() != null && !newFilm.getReleaseDate().equals(oldFilm.getReleaseDate())) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.info("Дата релиза изменена на {}", newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null && !newFilm.getDuration().equals(oldFilm.getDuration())) {
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Продолжительность фильма изменена на {}", newFilm.getDuration());
        }
        log.info("Фильм обновлён");
        return oldFilm;
    }

    private long getNextId() {
        long lastId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++lastId;
    }
}
