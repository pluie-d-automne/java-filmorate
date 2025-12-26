package ru.yandex.practicum.filmorate.storage;

import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected boolean delete(String query, Object... params) {
        int rowsDeleted = jdbc.update(query, params);
        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalException("Не удалось обновить данные");
        }
    }

    protected void batchInsert(String query, BatchPreparedStatementSetter batchPreparedStatementSetter) {
        jdbc.batchUpdate(query, batchPreparedStatementSetter);
    }

    protected void insert(String query, Object... params) {

        int rowsInserted = jdbc.update(query, params);
        if (rowsInserted == 0) {
            throw new InternalException("Не удалось сохранить данные");
        }
    }
}
