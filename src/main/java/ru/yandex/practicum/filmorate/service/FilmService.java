package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Service
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    private final GenreStorage genreStorage;


    public FilmService(
            @Qualifier("filmDbStorage")FilmStorage filmStorage,
            GenreStorage genreStorage
    ) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> newFilms = new ArrayList<>();
        for (Film film : filmStorage.getAllFilms()) {
            newFilms.add(updateGenres(film));
        }
        return newFilms;
    }

    public Film getFilmById(long filmId) {
        return updateGenres(filmStorage.getFilmById(filmId));
    }

    public Film create(Film newFilm) {
        return filmStorage.create(newFilm);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void like(Long filmId, Long userId) {
        filmStorage.like(filmId, userId);
    }

    public void unlike(Long filmId, Long userId) {
        filmStorage.unlike(filmId, userId);
    }

    public Collection<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    public void delete(Long filmId) {
        filmStorage.delete(filmId);
    }

    private Film updateGenres(Film film) {
        if (film.getGenres() != null) {
            Collection<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            List<Genre> updatedGenres = genreStorage.getAllGenre()
                    .stream()
                    .filter(genre -> genreIds.contains(genre.getId()))
                    .sorted(Comparator.comparing(Genre::getId))
                    .toList();
            film.setGenres(updatedGenres);
        }

       return film;
    }
}
