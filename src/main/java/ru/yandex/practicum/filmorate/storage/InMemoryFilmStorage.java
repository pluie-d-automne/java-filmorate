package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Comparator<Film> filmLikesComparator = Comparator.comparing(Film::getLikesCnt).reversed();

    @Override
    public Collection<Film> getAllFilms() {
        log.trace("Возвращен список всех фильмов");
        return films.values();
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            String message = "Фильма с id=" + filmId + " не существует.";
            throw new NotFoundException(message);
        }
    }

    @Override
    public Film create(Film newFilm) {
        newFilm.setId(getNextId());
        log.info("Фильму присвоен id={}", newFilm.getId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм добавлен.");
        return newFilm;
    }

    @Override
    public Film update(Film newFilm) {
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

    @Override
    public Film delete(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }

        Film deletedFilm = films.get(filmId);
        films.remove(filmId);
        log.info("Фильм с id={} удалён.", filmId);
        return deletedFilm;
    }

    @Override
    public void like(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        Set<Long> filmLikes = (film.getLikes() == null) ? new HashSet<>() : film.getLikes();
        filmLikes.add(userId);
        film.setLikes(filmLikes);
    }

    @Override
    public void unlike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        Set<Long> filmLikes = (film.getLikes() == null) ? new HashSet<>() :  film.getLikes();
        filmLikes.remove(userId);
        film.setLikes(filmLikes);
    }

    @Override
    public Collection<Film> getTopFilms(int count) {
        List<Film> filmsSorted = getAllFilms()
                .stream()
                .sorted(filmLikesComparator)
                .toList();
        if (filmsSorted.size() < count) {
            return filmsSorted;
        }
        return filmsSorted.subList(0, count);

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
