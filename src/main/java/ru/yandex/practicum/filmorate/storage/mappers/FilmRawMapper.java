package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FilmRawMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        Mpa mpa = new Mpa();
        List<Genre> genres = new ArrayList<>();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_dt").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        if (resultSet.getArray("genres") != null) {
            for (Integer genreId : (List<Integer>) resultSet.getArray("genres")) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genres.add(genre);
            }
            film.setGenres(genres);
        }
        film.setLikesCnt(resultSet.getInt("likes_cnt"));
        Integer mpaId = resultSet.getInt("rating_id");
        mpa.setId(mpaId);
        film.setMpa(mpa);
        return film;
    }
}
