package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Service
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    private final GenreStorage genreStorage;

    private final DirectorStorage directorStorage;

    public FilmService(
            @Qualifier("filmDbStorage")FilmStorage filmStorage,
            GenreStorage genreStorage,
            DirectorStorage directorStorage
    ) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> newFilms = new ArrayList<>();
        for (Film film : filmStorage.getAllFilms()) {
            newFilms.add(updateDirectors(updateGenres(film)));
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
        Collection<Film> newFilms = new ArrayList<>();
        for (Film film : filmStorage.getTopFilms(count)) {
            newFilms.add(updateDirectors(updateGenres(film)));
        }
        return newFilms;
    }

    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {
        Collection<Film> newFilms = new ArrayList<>();
        for (Film film : filmStorage.getFilmsByDirector(directorId, sortBy)) {
            newFilms.add(updateDirectors(updateGenres(film)));
        }
        return newFilms;
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

    private Film updateDirectors(Film film) {
        if (film.getDirectors() != null && ! film.getDirectors().isEmpty()) {
            Collection<Long> directorIds = film.getDirectors().stream().map(Director::getId).toList();
            List<Director> updatedDirector = directorStorage.getAllDirectors()
                    .stream()
                    .filter(director -> directorIds.contains(director.getId()))
                    .sorted(Comparator.comparing(Director::getId))
                    .toList();
            film.setDirectors(updatedDirector);
        }

        return film;
    }
}
