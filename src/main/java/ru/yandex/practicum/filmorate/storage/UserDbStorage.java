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
    private static final String UPDATE_QUERY = "UPDATE \"users\" SET \"email\" = ?, \"login\" = ?, \"name\" = " +
            "COALESCE(?, \"name\") , \"birthday\" = ? WHERE \"id\" = ?";
    private static final String DELETE_QUERY = "DELETE FROM \"users\" WHERE \"id\" = ?";
    private static final String FIND_USER_BY_ID = "SELECT * FROM \"users\" WHERE \"id\" = ?";
    private static final String ADD_FRIEND = "INSERT INTO \"friendships\" (\"user_id\", \"friend_id\") VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM \"friendships\" WHERE \"user_id\" = ? AND \"friend_id\" = ?";
    private static final String GET_ALL_FRIENDS = "SELECT * FROM \"users\" WHERE \"id\" IN (SELECT \"friend_id\" " +
            "FROM \"friendships\" WHERE \"user_id\" = ? )";
    private static final String GET_COMMON_FRIENDS = "SELECT * FROM \"users\" WHERE \"id\" IN (SELECT \"friend_id\" " +
            "FROM \"friendships\" WHERE \"user_id\" = ? INTERSECT SELECT \"friend_id\" FROM \"friendships\" WHERE \"user_id\" = ?)";
    private static final String FIND_MOST_SIMILAR_USER = "SELECT \"other_user_id\" " +
            "FROM ( " +
            "    SELECT " +
            "        fl2.\"user_id\" AS \"other_user_id\", " +
            "        COUNT(DISTINCT fl1.\"film_id\") AS \"common_likes\" " +
            "    FROM \"film_likes\" fl1 " +
            "    JOIN \"film_likes\" fl2 ON fl1.\"film_id\" = fl2.\"film_id\" " +
            "    WHERE fl1.\"user_id\" = ? " +
            "        AND fl2.\"user_id\" != ? " +
            "    GROUP BY fl2.\"user_id\" " +
            "    ORDER BY \"common_likes\" DESC " +
            "    LIMIT 1 " +
            ") AS \"similar_users\"";


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
            Optional<User> updatedUser = findOne(FIND_USER_BY_ID, user.getId());
            if (updatedUser.isPresent()) {
                return updatedUser.get();
            } else {
                throw new NotFoundException("Пользователь с id=" + user.getId() + " пропал.");
            }
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
        Optional<User> user = findOne(FIND_USER_BY_ID, userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        Optional<User> user = findOne(FIND_USER_BY_ID, userId);
        Optional<User> friend = findOne(FIND_USER_BY_ID, friendId);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        } else if (friend.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден.");
        } else {
            insert(
                    ADD_FRIEND,
                    userId,
                    friendId
            );
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        Optional<User> user = findOne(FIND_USER_BY_ID, userId);
        Optional<User> friend = findOne(FIND_USER_BY_ID, friendId);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        } else if (friend.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден.");
        } else {
            delete(DELETE_FRIEND, userId, friendId);
        }
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        Optional<User> user = findOne(FIND_USER_BY_ID, userId);
        if (user.isPresent()) {
            return findMany(GET_ALL_FRIENDS, userId);
        } else {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        Optional<User> user = findOne(FIND_USER_BY_ID, userId);
        Optional<User> otherUser = findOne(FIND_USER_BY_ID, otherUserId);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        } else if (otherUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + otherUserId + " не найден.");
        } else {
            return findMany(GET_COMMON_FRIENDS, userId, otherUserId);
        }
    }

    @Override
    public Long findMostSimilarUser(Long userId) {
        log.trace("Ищем пользователя с похожими вкусами для пользователя с ID: {}", userId);

        try {
            return jdbc.queryForObject(
                    FIND_MOST_SIMILAR_USER,
                    Long.class,
                    userId,
                    userId
            );
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            log.debug("Для пользователя с ID {} не найден пользователь с похожими вкусами", userId);
            return null;
        }
    }
}
