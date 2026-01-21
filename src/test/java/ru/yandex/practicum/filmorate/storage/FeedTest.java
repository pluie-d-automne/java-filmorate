package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.mappers.FeedRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FeedDbStorage.class, FeedRowMapper.class})
class FeedTest {

    @Autowired
    private FeedDbStorage feedDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long userId1;
    private Long userId2;

    @BeforeEach
    void setUp() {
        // очистка
        cleanDatabase();

        //  тестовые данные
        createTestUsers();

        // Получаем ID созданных пользователей
        userId1 = getUserIdByEmail("user1@test.com");
        userId2 = getUserIdByEmail("user2@test.com");
    }

    private void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM \"user_feeds\"");
        jdbcTemplate.update("DELETE FROM \"review_reactions\"");
        jdbcTemplate.update("DELETE FROM \"reviews\"");
        jdbcTemplate.update("DELETE FROM \"film_directors\"");
        jdbcTemplate.update("DELETE FROM \"film_likes\"");
        jdbcTemplate.update("DELETE FROM \"friendships\"");
        jdbcTemplate.update("DELETE FROM \"film_genres\"");
        jdbcTemplate.update("DELETE FROM \"films\"");
        jdbcTemplate.update("DELETE FROM \"directors\"");
        jdbcTemplate.update("DELETE FROM \"users\"");
        jdbcTemplate.update("DELETE FROM \"genres\"");
        jdbcTemplate.update("DELETE FROM \"ratings\"");

        // Сбрасываем sequence для user_feeds
        //jdbcTemplate.execute("ALTER TABLE \"user_feeds\" ALTER COLUMN \"event_id\" RESTART WITH 1");
    }

    private void createTestUsers() {
        // Создаем пользователей
        String insertUser1 = "INSERT INTO \"users\" (\"email\", \"login\", \"name\", \"birthday\") VALUES (?, ?, ?, ?)";
        String insertUser2 = "INSERT INTO \"users\" (\"email\", \"login\", \"name\", \"birthday\") VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(insertUser1, "user1@test.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        jdbcTemplate.update(insertUser2, "user2@test.com", "user2", "User Two", LocalDate.of(1991, 1, 1));
    }

    private Long getUserIdByEmail(String email) {
        try {
            String sql = "SELECT \"id\" FROM \"users\" WHERE \"email\" = ?";
            return jdbcTemplate.queryForObject(sql, Long.class, email);
        } catch (Exception e) {
            throw new RuntimeException("User with email " + email + " not found", e);
        }
    }

    @Test
    void getUserFeed_ShouldReturnEmptyList_WhenNoEvents() {
        List<UserFeedEvent> result = feedDbStorage.getUserFeed(userId1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addEvent_ShouldAddEventToDatabase() {
        UserFeedEvent event = UserFeedEvent.builder()
                .userId(userId1)
                .eventType(UserFeedEvent.EventType.LIKE)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(10L)
                .build();

        feedDbStorage.addEvent(event);

        List<UserFeedEvent> events = feedDbStorage.getUserFeed(userId1);
        assertNotNull(events);
        assertEquals(1, events.size());

        UserFeedEvent savedEvent = events.get(0);
        assertEquals(event.getUserId(), savedEvent.getUserId());
        assertEquals(event.getEventType(), savedEvent.getEventType());
        assertEquals(event.getOperation(), savedEvent.getOperation());
        assertEquals(event.getEntityId(), savedEvent.getEntityId());
        assertNotNull(savedEvent.getEventId());
        assertNotNull(savedEvent.getTimestamp());
    }

    @Test
    void getUserFeed_ShouldReturnEventsInChronologicalOrder() {
        UserFeedEvent event1 = UserFeedEvent.builder()
                .userId(userId1)
                .eventType(UserFeedEvent.EventType.FRIEND)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(userId2)
                .build();

        UserFeedEvent event2 = UserFeedEvent.builder()
                .userId(userId1)
                .eventType(UserFeedEvent.EventType.LIKE)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(10L)
                .build();

        feedDbStorage.addEvent(event2);
        feedDbStorage.addEvent(event1);

        List<UserFeedEvent> result = feedDbStorage.getUserFeed(userId1);

        assertEquals(2, result.size());

        assertTrue(result.get(0).getTimestamp() <= result.get(1).getTimestamp());
    }

    @Test
    void getUserFeed_ShouldReturnOnlyUserEvents() {
        UserFeedEvent user1Event = UserFeedEvent.builder()
                .userId(userId1)
                .eventType(UserFeedEvent.EventType.LIKE)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(10L)
                .build();

        UserFeedEvent user2Event = UserFeedEvent.builder()
                .userId(userId2)
                .eventType(UserFeedEvent.EventType.FRIEND)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(3L)
                .build();

        feedDbStorage.addEvent(user1Event);
        feedDbStorage.addEvent(user2Event);

        List<UserFeedEvent> user1Events = feedDbStorage.getUserFeed(userId1);
        List<UserFeedEvent> user2Events = feedDbStorage.getUserFeed(userId2);

        assertEquals(1, user1Events.size());
        assertEquals(userId1, user1Events.get(0).getUserId());

        assertEquals(1, user2Events.size());
        assertEquals(userId2, user2Events.get(0).getUserId());
    }

    @Test
    void addEvent_WithAllEventTypes_ShouldWorkCorrectly() {
        List<UserFeedEvent> events = List.of(
                UserFeedEvent.builder()
                        .userId(userId1)
                        .eventType(UserFeedEvent.EventType.LIKE)
                        .operation(UserFeedEvent.OperationType.ADD)
                        .entityId(10L)
                        .build(),
                UserFeedEvent.builder()
                        .userId(userId1)
                        .eventType(UserFeedEvent.EventType.REVIEW)
                        .operation(UserFeedEvent.OperationType.ADD)
                        .entityId(20L)
                        .build(),
                UserFeedEvent.builder()
                        .userId(userId1)
                        .eventType(UserFeedEvent.EventType.FRIEND)
                        .operation(UserFeedEvent.OperationType.ADD)
                        .entityId(userId2)
                        .build(),
                UserFeedEvent.builder()
                        .userId(userId1)
                        .eventType(UserFeedEvent.EventType.LIKE)
                        .operation(UserFeedEvent.OperationType.REMOVE)
                        .entityId(10L)
                        .build(),
                UserFeedEvent.builder()
                        .userId(userId1)
                        .eventType(UserFeedEvent.EventType.REVIEW)
                        .operation(UserFeedEvent.OperationType.UPDATE)
                        .entityId(20L)
                        .build()
        );

        for (UserFeedEvent event : events) {
            assertDoesNotThrow(() -> feedDbStorage.addEvent(event));
        }

        List<UserFeedEvent> savedEvents = feedDbStorage.getUserFeed(userId1);
        assertEquals(5, savedEvents.size());

        // Проверяем, что все события сохранены
        assertTrue(savedEvents.stream().allMatch(event ->
                event.getUserId().equals(userId1) &&
                        event.getEventId() != null &&
                        event.getTimestamp() != null
        ));
    }
}