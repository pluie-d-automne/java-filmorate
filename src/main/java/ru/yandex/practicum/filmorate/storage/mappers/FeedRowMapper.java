package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<UserFeedEvent> {
    @Override
    public UserFeedEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserFeedEvent.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getLong("user_id"))
                .eventType(UserFeedEvent.EventType.valueOf(rs.getString("event_type")))
                .operation(UserFeedEvent.OperationType.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }

}
