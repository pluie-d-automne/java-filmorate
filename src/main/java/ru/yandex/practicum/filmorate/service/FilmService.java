package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    Comparator<Film> filmLikesComparator = Comparator.comparing(Film::getLikesCnt).reversed();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
