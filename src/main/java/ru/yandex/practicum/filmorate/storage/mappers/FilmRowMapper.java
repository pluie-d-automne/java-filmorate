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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        List<Genre> genres = new ArrayList<>();
        List<Director> directors = new ArrayList<>();
        LocalDate releaseDate = null;
        Mpa mpa = null;

        if (resultSet.getDate("release_dt") != null) {
            releaseDate = resultSet.getDate("release_dt").toLocalDate();
        }

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

        Integer mpaId = resultSet.getInt("rating_id");
        String mpaName = resultSet.getString("mpa_name");
        if (mpaName != null) {
            mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(mpaName);
        }

        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .genres(genres)
                .releaseDate(releaseDate)
                .directors(directors)
                .likesCnt(resultSet.getInt("likes_cnt"))
                .mpa(mpa)
                .build();
    }
}
