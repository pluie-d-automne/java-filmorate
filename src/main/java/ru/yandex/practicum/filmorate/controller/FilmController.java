package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(InMemoryFilmStorage inMemoryFilmStorage,  FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(@RequestParam(defaultValue = "10") String count) {
        return filmService.getTopFilms(Integer.parseInt(count));
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public Film create(@Valid @RequestBody Film newFilm) {
        return filmService.create(newFilm);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void like(
            @PathVariable Long filmId,
            @PathVariable Long userId
    ) {
        filmService.like(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void unlike(
            @PathVariable Long filmId,
            @PathVariable Long userId
    ) {
        filmService.unlike(filmId, userId);
    }


}
