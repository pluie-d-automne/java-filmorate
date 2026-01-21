package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

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

    @Override
    public Film updateGenres(Film film) {
        List<Genre> updatedGenres = new ArrayList<>();
        if (film.getGenres() != null) {
            Collection<Integer> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            updatedGenres = getAllGenre()
                    .stream()
                    .filter(genre -> genreIds.contains(genre.getId()))
                    .sorted(Comparator.comparing(Genre::getId))
                    .toList();

        }

        film.setGenres(updatedGenres);
        return film;
    }

}
