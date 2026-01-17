package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Director create(Director director);

    Director update(Director director);

    Director delete(Long directorId);

    Director getDirectorById(Long directorId);

    Collection<Director> getAllDirectors();
}
