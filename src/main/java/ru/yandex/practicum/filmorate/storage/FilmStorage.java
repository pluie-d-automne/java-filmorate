package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(Long filmId);

    Film create(Film newFilm);

    Film update(Film newFilm);

    Film delete(Long filmId);

    void like(Long filmId, Long userId);

    void unlike(Long filmId, Long userId);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    Collection<Film> getFilmsByDirector(Long directorId, String sortBy);

    List<Film> getFilmsLikedByUserButNotByOther(Long sourceUserId, Long targetUserId);

    List<Film> getCommonFilms(long userId, long friendId);
}
