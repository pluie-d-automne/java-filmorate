package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage{
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"ratings\"";
    private static final String FIND_MPA_BY_ID = "SELECT * FROM \"ratings\" WHERE \"id\" = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        log.trace("Получаем список всех рейтингов MPA");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Mpa getMpaById(Integer mpaId) {
        Optional<Mpa> mpa = findOne(FIND_MPA_BY_ID, mpaId);
        if (mpa.isPresent()) {
            return mpa.get();
        } else {
            throw new NotFoundException("MPA с id=" + mpaId + " не найден.");
        }
    }
}
