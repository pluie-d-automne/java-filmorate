package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"users\"";
    private static final String INSERT_QUERY = "INSERT INTO \"users\" (\"email\", \"login\", \"name\", \"birthday\") " +
             "VALUES (?, ?, ?, ?)";
    private static final String FIND_USER_BY_LOGIN = "SELECT * FROM \"users\" WHERE \"login\" = ?";
    private static final String UPDATE_QUERY = "UPDATE \"users\" SET \"email\" = ?, \"login\" = ?, \"name\" = ?, " +
        "\"birthday\" = ? WHERE \"id\" = ?";
    private static final String DELETE_QUERY = "DELETE FROM \"users\" WHERE id = ?";
    private static final String FIND_USER_BY_ID = "SELECT * FROM \"users\" WHERE \"id\" = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getAllUsers() {
        log.trace("Получаем список всех пользователей");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя пустое - будет использован логин");
            user.setName(user.getLogin());
        }
        insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        Optional<User> createdUser = findOne(FIND_USER_BY_LOGIN, user.getLogin());
        if (createdUser.isPresent()) {
            Long id = createdUser.get().getId();
            user.setId(id);
        }
        return user;
    }

    @Override
    public User update(User user) {
        Optional<User> oldUser = findOne(FIND_USER_BY_ID, user.getId());
        if (oldUser.isPresent()) {
            update(
                    UPDATE_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId()
            );
            return user;
        } else {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден.");
        }
    }

    @Override
    public User delete(Long userId) {
        Optional<User> user = findOne(FIND_USER_BY_ID, userId);
        if (user.isPresent()) {
            delete(DELETE_QUERY, userId);
            return user.get();
        } else {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> user = findOne(FIND_USER_BY_ID,userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }
}
