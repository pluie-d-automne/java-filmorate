package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedStorage feedStorage;

    public void addFriendEvent(Long userId, Long friendId) {
        UserFeedEvent event = UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.FRIEND)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(friendId)
                .build();
        feedStorage.addEvent(event);
    }

    public void removeFriendEvent(Long userId, Long friendId) {
        UserFeedEvent event = UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.FRIEND)
                .operation(UserFeedEvent.OperationType.REMOVE)
                .entityId(friendId)
                .build();
        feedStorage.addEvent(event);
    }

    public void addReviewEvent(Long userId, Long reviewId) {
        UserFeedEvent event = UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.REVIEW)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(reviewId)
                .build();
        feedStorage.addEvent(event);
    }

    public void updateReviewEvent(Long userId, Long reviewId) {
        UserFeedEvent event = UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.REVIEW)
                .operation(UserFeedEvent.OperationType.UPDATE)
                .entityId(reviewId)
                .build();
        feedStorage.addEvent(event);
    }

    public void removeReviewEvent(Long userId, Long reviewId) {
        UserFeedEvent event = UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.REVIEW)
                .operation(UserFeedEvent.OperationType.REMOVE)
                .entityId(reviewId)
                .build();
        feedStorage.addEvent(event);
    }

    public void addLikeEvent(Long userId, Long filmId) {
        UserFeedEvent event = UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.LIKE)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(filmId)
                .build();
        feedStorage.addEvent(event);
    }

    public void removeLikeEvent(Long userId, Long filmId) {
        UserFeedEvent event = UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.LIKE)
                .operation(UserFeedEvent.OperationType.REMOVE)
                .entityId(filmId)
                .build();
        feedStorage.addEvent(event);
    }

    public List<UserFeedEvent> getUserFeed(Long userId) {
        return feedStorage.getUserFeed(userId);
    }
}