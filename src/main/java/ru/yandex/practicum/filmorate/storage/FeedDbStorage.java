package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.mappers.FeedRowMapper;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbc;

    static final String GET_USER_FEED_QUERY =
            "SELECT * FROM \"user_feeds\" WHERE \"user_id\" = ? ORDER BY \"timestamp\"";

    // лента событий друзей пользователя
    //         "SELECT uf.* " +
    //                 "FROM \"user_feeds\" uf " +
    //                 "WHERE uf.\"user_id\" IN (" +
    //                        "SELECT f.\"friend_id\" " +
    //                            "FROM \"friendships\" f " +
    //                            "WHERE f.\"user_id\" = ? " +
    //                    "UNION " +
    //                        "SELECT f.\"user_id\" " +
    //                            "FROM \"friendships\" f " +
    //                            "WHERE f.\"friend_id\" = ?) " +
    //                 "ORDER BY uf.\"timestamp\" DESC";        //в какую сторону сортировать

    private static final String INSERT_EVENT_QUERY =
            "INSERT INTO \"user_feeds\" (\"timestamp\", \"user_id\", \"event_type\", \"operation\", \"entity_id\") " +
                    "VALUES (?, ?, ?, ?, ?)";

    @Override
    public List<UserFeedEvent> getUserFeed(Long userId) {
        log.debug("Получаем ленту событий для пользователя с id={}", userId);

        return jdbc.query(GET_USER_FEED_QUERY, new FeedRowMapper(), userId);

        //лента событий друзей пользователя
        //return jdbc.query(GET_USER_FEED_QUERY, new FeedRowMapper(), userId, userId);
    }

    @Override
    public void addEvent(UserFeedEvent event) {
        log.debug("Добавляем событие в ленту: {}", event);
        jdbc.update(INSERT_EVENT_QUERY,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId());
    }
}