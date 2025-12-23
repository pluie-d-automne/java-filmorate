package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Component
public class FilmRawMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_dt").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setGenres((Set<Long>) resultSet.getArray("genres"));
        film.setLikesCnt(resultSet.getInt("likes_cnt"));
        film.setRating(resultSet.getInt("rating_id"));
        return film;
    }
}
