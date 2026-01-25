package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Qualifier("filmDbStorage")
    private final FilmService filmService;

    private final FeedService feedService;
    private final GenreService genreService;
    private final DirectorService directorService;

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
        feedService.addFriendEvent(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
        feedService.removeFriendEvent(userId, friendId);
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

        for (Film film : filmService.getFilmsLikedByUserButNotByOther(similarUserId, userId)) {
            recommendedFilms.add(directorService.updateDirectors(genreService.updateGenres(film)));
        }

        return recommendedFilms;
    }

    public Collection<UserFeedEvent> getUserFeed(Long userId) {
        getUserById(userId);

        return feedService.getUserFeed(userId);
    }
}
