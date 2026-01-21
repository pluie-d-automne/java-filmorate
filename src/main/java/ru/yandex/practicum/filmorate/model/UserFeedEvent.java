package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedEvent {
    private Long eventId;

    @NotNull
    private Long timestamp;

    @NotNull
    private Long userId;

    @NotNull
    private EventType eventType;

    @NotNull
    private OperationType operation;

    @NotNull
    private Long entityId;

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum OperationType {
        REMOVE,
        ADD,
        UPDATE
    }

    public static UserFeedEventBuilder builder() {
        return new UserFeedEventBuilder()
                .timestamp(Instant.now().toEpochMilli());
    }
}