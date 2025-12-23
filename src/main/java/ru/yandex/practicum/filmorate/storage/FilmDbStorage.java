package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"films_full\"";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.trace("Получаем список всех фильмов");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film getFilmById(Long filmId) {
        return new Film();
    }

    @Override
    public Film create(Film film) {
        return film;
    }

    @Override
    public Film update(Film film) {
        return film;
    }

    @Override
    public Film delete(Long filmId) {
        return new Film();
    }

}
