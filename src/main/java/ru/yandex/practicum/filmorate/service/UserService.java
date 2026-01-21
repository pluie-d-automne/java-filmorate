package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("filmDbStorage")
    FilmStorage filmStorage;
    private final FeedStorage feedStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            FeedStorage feedStorage,
            GenreStorage genreStorage,
            DirectorStorage directorStorage
    ) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long userId) {
        return userStorage.getUserById(userId);
    }

    public User create(User newUser) {
        return userStorage.create(newUser);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);

        feedStorage.addEvent(UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.FRIEND)
                .operation(UserFeedEvent.OperationType.ADD)
                .entityId(friendId)
                .build());
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);

        feedStorage.addEvent(UserFeedEvent.builder()
                .userId(userId)
                .eventType(UserFeedEvent.EventType.FRIEND)
                .operation(UserFeedEvent.OperationType.REMOVE)
                .entityId(friendId)
                .build());
    }

    public Collection<User> getUserFriends(Long userId) {
        return userStorage.getUserFriends(userId);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    public Collection<Film> getRecommendations(Long userId) {
        userStorage.getUserById(userId);

        Long similarUserId = userStorage.findMostSimilarUser(userId);

        if (similarUserId == null) {
            return List.of();
        }

        Collection<Film> recommendedFilms = new ArrayList<>();

        for (Film film : filmStorage.getFilmsLikedByUserButNotByOther(similarUserId, userId)) {
            recommendedFilms.add(directorStorage.updateDirectors(genreStorage.updateGenres(film)));
        }

        return recommendedFilms;
    }

    public Collection<UserFeedEvent> getUserFeed(Long userId) {
        getUserById(userId);
        return feedStorage.getUserFeed(userId);
    }
}
