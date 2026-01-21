package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    private final GenreStorage genreStorage;

    private final DirectorStorage directorStorage;

    private final FeedStorage feedStorage;

    private final UserStorage userStorage;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            GenreStorage genreStorage,
            DirectorStorage directorStorage,
            FeedStorage feedStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
        this.feedStorage = feedStorage;
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> newFilms = new ArrayList<>();
        for (Film film : filmStorage.getAllFilms()) {
            newFilms.add(directorStorage.updateDirectors(genreStorage.updateGenres(film)));
        }
        return newFilms;
    }

    public Film getFilmById(long filmId) {
        return directorStorage.updateDirectors(genreStorage.updateGenres(filmStorage.getFilmById(filmId)));
    }

    public Film create(Film newFilm) {
        return filmStorage.create(newFilm);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void like(Long filmId, Long userId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
        filmStorage.like(filmId, userId);

        feedStorage.addEvent(UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.LIKE)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(filmId)
                .build());
    }

    public void unlike(Long filmId, Long userId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
        filmStorage.unlike(filmId, userId);

        feedStorage.addEvent(UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.LIKE)
                .operation(UserFeedEvent.OperationType.REMOVE)
                .entityId(filmId)
                .build());
    }

    public void delete(Long filmId) {
        filmStorage.delete(filmId);
    }

    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {
        Collection<Film> newFilms = new ArrayList<>();
        for (Film film : filmStorage.getFilmsByDirector(directorId, sortBy)) {
            newFilms.add(directorStorage.updateDirectors(genreStorage.updateGenres(film)));
        }
        return newFilms;
    }

    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Film> films = filmStorage.getPopularFilms(count, genreId, year);

        Collection<Film> newFilms = new ArrayList<>();
        for (Film film : films) {
            newFilms.add(directorStorage.updateDirectors(genreStorage.updateGenres(film)));
        }
        return newFilms;
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public Collection<Film> searchFilms(String query, List<String> searchBy) {
        List<Film> films = filmStorage.searchFilms(query, searchBy);

        Collection<Film> newFilms = new ArrayList<>();
        for (Film film : films) {
            newFilms.add(directorStorage.updateDirectors(genreStorage.updateGenres(film)));
        }
        return newFilms;
    }
}