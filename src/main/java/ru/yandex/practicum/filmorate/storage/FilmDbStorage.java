package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"films_full\"";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM \"films_full\" WHERE \"id\" = ?";
    private static final String DELETE_FILM_BY_ID = "DELETE FROM \"films\" WHERE \"id\" = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO \"films\" (\"name\", \"description\", \"release_dt\", " +
            "\"duration\", \"rating_id\") VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_FILM_BY_NAME = "SELECT * FROM \"films_full\" WHERE \"name\" = ? AND " +
            "COALESCE(\"release_dt\", '9999-01-01') = COALESCE(?, '9999-01-01') ORDER BY \"id\" DESC LIMIT 1";
    private static final String INSERT_GENRE_LINK_QUERY = "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\") " +
            "VALUES (?, ?)";
    private static final String INSERT_DIRECTORS_QUERY = "INSERT INTO \"film_directors\" (\"film_id\", \"director_id\") " +
            "VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE \"films\" SET \"name\" = ?, \"description\" = ?, \"release_dt\" = ?, " +
            "\"duration\" = ?, \"rating_id\" = ? WHERE \"id\" = ?";
    private static final String  DELETE_FILM_GENRES_BY_ID = "DELETE FROM \"film_genres\" WHERE \"film_id\" = ?";
    private static final String  DELETE_FILM_DIRECTORS_BY_ID = "DELETE FROM \"film_directors\" WHERE \"film_id\" = ?";
    private static final String LIKE_FILM = "INSERT INTO \"film_likes\" (\"film_id\", \"user_id\") " +
            "VALUES (?, ?)";
    private static final String UNLIKE_FILM = "DELETE FROM \"film_likes\" WHERE \"film_id\" = ? AND \"user_id\" = ?";
    private static final String TOP_FILMS = "SELECT * FROM \"films_full\" ORDER BY \"likes_cnt\" DESC LIMIT ?";

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
        Optional<Film> film = findOne(FIND_FILM_BY_ID, filmId);
        if (film.isPresent()) {
            return film.get();
        } else {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
    }

    @Override
    public Film create(Film film) {
        Mpa mpa = film.getMpa();
        Integer mpaId = mpa != null ? mpa.getId() : null;

        insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId
        );

        Optional<Film> createdFilm = findOne(FIND_FILM_BY_NAME, film.getName(), film.getReleaseDate());

        if (createdFilm.isPresent()) {
            Long id = createdFilm.get().getId();
            updateFilmDirectors(film.getDirectors(), id);
            updateFilmGenres(film.getGenres(), id);
            film.setId(id);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        Long filmId = film.getId();
        log.trace("Request to change film with id=" + filmId);
        Optional<Film> oldFilm = findOne(FIND_FILM_BY_ID, filmId);
        if (oldFilm.isPresent()) {
            Mpa mpa = film.getMpa();
            Integer mpaId = mpa != null ? mpa.getId() : null;
            update(
                    UPDATE_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    mpaId,
                    filmId
            );
            updateFilmGenres(film.getGenres(), filmId);
            updateFilmDirectors(film.getDirectors(), filmId);
            return film;
        } else {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден.");
        }
    }

    @Override
    public Film delete(Long filmId) {
        Optional<Film> film = findOne(FIND_FILM_BY_ID, filmId);
        if (film.isPresent()) {
            delete(DELETE_FILM_BY_ID, filmId);
            return film.get();
        } else {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
    }

    @Override
    public void like(Long filmId, Long userId) {
        insert(LIKE_FILM, filmId, userId);
        log.info("Добавлен лайк пользователем " + userId + " фильму " + filmId);
    }

    @Override
    public void unlike(Long filmId, Long userId) {
        delete(UNLIKE_FILM, filmId, userId);
        log.info("Удалён лайк пользователя " + userId + " фильму " + filmId);
    }

    @Override
    public Collection<Film> getTopFilms(int count) {
        return findMany(TOP_FILMS, count);
    }

    @Override
    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {
        String query;
        if (sortBy.equals("likes")) {
            query = DIRECTOR_FILMS_BY_LIKES;
        } else {
            query = DIRECTOR_FILMS_BY_DT;
        }
        return findMany(query, directorId);
    }

    void updateFilmGenres(Collection<Genre> fullGenres, Long filmId) {
        if (fullGenres != null && !fullGenres.isEmpty()) {
            Set<Genre> genres = new HashSet<>(fullGenres);
            delete(DELETE_FILM_GENRES_BY_ID, filmId);
            batchInsert(INSERT_GENRE_LINK_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Genre genre =  genres.stream().toList().get(i);
                    ps.setLong(1, filmId);
                    ps.setInt(2, genre.getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        }
    }

    void updateFilmDirectors(Collection<Director> allDirectors, Long filmId) {
        if (allDirectors != null && !allDirectors.isEmpty()) {
            Set<Director> directors = new HashSet<>(allDirectors);
            delete(DELETE_FILM_DIRECTORS_BY_ID, filmId);
            batchInsert(INSERT_DIRECTORS_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Director director =  directors.stream().toList().get(i);
                    ps.setLong(1, filmId);
                    ps.setLong(2, director.getId());
                }

                @Override
                public int getBatchSize() {
                    return directors.size();
                }
            });
        }
    }

}
