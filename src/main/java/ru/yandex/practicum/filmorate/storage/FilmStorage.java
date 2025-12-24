package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(Long filmId);

    Film create(Film newFilm);

    Film update(Film newFilm);

    Film delete(Long filmId);

    void like(Long filmId, Long userId);

    void unlike(Long filmId, Long userId);

    Collection<Film> getTopFilms(int count);
}
