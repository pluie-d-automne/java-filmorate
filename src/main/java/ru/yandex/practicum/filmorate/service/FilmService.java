package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    private final MpaStorage mpaStorage;
    private final Comparator<Film> filmLikesComparator = Comparator.comparing(Film::getLikesCnt).reversed();

    public FilmService(
            @Qualifier("filmDbStorage")FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaStorage mpaStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film create(Film newFilm) {
        if (newFilm.getMpa() != null) {
            mpaStorage.getMpaById(newFilm.getMpa().getId());
        }
        return filmStorage.create(newFilm);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void like(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        Set<Long> filmLikes = (film.getLikes() == null) ? new HashSet<>() :  film.getLikes();
        filmLikes.add(userId);
        film.setLikes(filmLikes);
    }

    public void unlike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        Set<Long> filmLikes = (film.getLikes() == null) ? new HashSet<>() :  film.getLikes();
        filmLikes.remove(userId);
        film.setLikes(filmLikes);
    }

    public Collection<Film> getTopFilms(int count) {
        List<Film> filmsSorted = filmStorage.getAllFilms()
                .stream()
                .sorted(filmLikesComparator)
                .toList();
        if (filmsSorted.size() < count) {
            return filmsSorted;
        }
        return filmsSorted.subList(0, count);

    }
}
