package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
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
        List<Director> directors = new ArrayList<>();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        if (resultSet.getDate("release_dt") != null) {
            film.setReleaseDate(resultSet.getDate("release_dt").toLocalDate());
        }
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
        }

        film.setGenres(genres);

        Array arrayDirector = resultSet.getArray("director");
        if (arrayDirector != null) {
            Object[] oArray = (Object[]) arrayDirector.getArray();

            for (Object o : oArray) {
                Long directorId = Long.parseLong(o.toString());
                Director director = new Director();
                director.setId(directorId);
                directors.add(director);
            }
        }

        film.setDirectors(directors);
        film.setLikesCnt(resultSet.getInt("likes_cnt"));
        Integer mpaId = resultSet.getInt("rating_id");
        String mpaName = resultSet.getString("mpa_name");
        if (mpaName != null) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(mpaName);
            film.setMpa(mpa);
        }
        return film;
    }
}
