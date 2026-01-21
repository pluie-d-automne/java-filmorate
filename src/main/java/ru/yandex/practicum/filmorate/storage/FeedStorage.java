package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.UserFeedEvent;

import java.util.List;

public interface FeedStorage {
    List<UserFeedEvent> getUserFeed(Long userId);

    void addEvent(UserFeedEvent event);
}