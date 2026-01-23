package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Repository
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"directors\"";
    private static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM \"directors\" WHERE \"id\" = ?";
    private static final String INSERT_QUERY = "INSERT INTO \"directors\" (\"name\") VALUES (?)";
    private static final String FIND_DIRECTOR_BY_NAME = "SELECT * FROM \"directors\" WHERE \"name\" = ?";
    private static final String UPDATE_QUERY = "UPDATE \"directors\" SET \"name\" = ? WHERE \"id\" = ?";
    private static final String DELETE_QUERY = "DELETE FROM \"directors\" WHERE \"id\" = ?";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Director create(Director director) {
        insert(
                INSERT_QUERY,
                director.getName()
        );
        Optional<Director> createdDirector = findOne(FIND_DIRECTOR_BY_NAME, director.getName());
        if (createdDirector.isPresent()) {
            Long id = createdDirector.get().getId();
            director.setId(id);
        }
        return director;
    }

    @Override
    public Director update(Director director) {
        Optional<Director> oldDirector = findOne(FIND_DIRECTOR_BY_ID, director.getId());
        if (oldDirector.isPresent()) {
            update(
                    UPDATE_QUERY,
                    director.getName(),
                    director.getId()
            );
            Optional<Director> updatedDirector = findOne(FIND_DIRECTOR_BY_ID, director.getId());
            if (updatedDirector.isPresent()) {
                return updatedDirector.get();
            } else {
                throw new NotFoundException("Режиссёр с id=" + director.getId() + " пропал.");
            }
        } else {
            throw new NotFoundException("Режиссёр с id=" + director.getId() + " не найден.");
        }
    }

    @Override
    public Director delete(Long directorId) {
        Optional<Director> director = findOne(FIND_DIRECTOR_BY_ID, directorId);
        if (director.isPresent()) {
            delete(DELETE_QUERY, directorId);
            return director.get();
        } else {
            throw new NotFoundException("Режиссёр с id=" + directorId + " не найден.");
        }
    }

    @Override
    public Director getDirectorById(Long directorId) {
        Optional<Director> director = findOne(FIND_DIRECTOR_BY_ID, directorId);
        if (director.isPresent()) {
            return director.get();
        } else {
            throw new NotFoundException("Режиссёр с id=" + directorId + " не найден.");
        }

    }

    @Override
    public Collection<Director> getAllDirectors() {
        log.trace("Получаем список всех режиссёров.");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film updateDirectors(Film film) {
        List<Director> updatedDirector = new ArrayList<>();
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            Collection<Long> directorIds = film.getDirectors().stream().map(Director::getId).toList();
            updatedDirector = getAllDirectors()
                    .stream()
                    .filter(director -> directorIds.contains(director.getId()))
                    .sorted(Comparator.comparing(Director::getId))
                    .toList();
        }

        film.setDirectors(updatedDirector);
        return film;
    }
}
