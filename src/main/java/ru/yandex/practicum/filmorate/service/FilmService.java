package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.*;

@Service
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;


    public FilmService(
            @Qualifier("filmDbStorage")FilmStorage filmStorage,
            MpaStorage mpaStorage,
            GenreStorage genreStorage
    ) {
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
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
        if (newFilm.getMpa() != null) {
            mpaStorage.getMpaById(newFilm.getMpa().getId());
        }
        if (newFilm.getGenres() != null) {
            for (Genre genre : newFilm.getGenres()) {
                genreStorage.getGenreById(genre.getId());
            }
        }
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

    private Film updateGenres(Film film) {
        Collection<Genre> genres = film.getGenres();

        if (genres != null) {
            Collection<Genre> newGenres = new ArrayList<>();

            for (Genre genre : genres) {
                genre = genreStorage.getGenreById(genre.getId());
                newGenres.add(genre);
            }

            film.setGenres(newGenres);
        }

       return film;
    }
}
