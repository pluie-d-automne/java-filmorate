package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"genres\" ORDER BY \"id\"";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM \"genres\" WHERE \"id\" = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> getAllGenre() {
        log.trace("Получаем список всех жанров");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        Optional<Genre> genre = findOne(FIND_GENRE_BY_ID, genreId);
        if (genre.isPresent()) {
            return genre.get();
        } else {
            throw new NotFoundException("Жанр с id=" + genreId + " не найден.");
        }
    }

}
