package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        List<Genre> genres = new ArrayList<>();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_dt").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        Array array = resultSet.getArray("genres");
        if (array != null) {
            Object[] oArray = (Object[]) array.getArray();

            for (Object o : oArray) {
                Integer genreId = Integer.parseInt(o.toString());
                Genre genre = new Genre();
                genre.setId(genreId);
                genres.add(genre);
            }

            film.setGenres(genres);
        }
        film.setLikesCnt(resultSet.getInt("likes_cnt"));
        Integer mpaId = resultSet.getInt("rating_id");
        String mpaName = resultSet.getString("mpa_name");
        Mpa mpa = new Mpa();
        mpa.setId(mpaId);
        mpa.setName(mpaName);
        film.setMpa(mpa);
        return film;
    }
}
