package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("filmDbStorage")
    FilmStorage filmStorage;

    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
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
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
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

    public List<Film> getRecommendations(Long userId) {
        userStorage.getUserById(userId);

        Long similarUserId = userStorage.findMostSimilarUser(userId);

        if (similarUserId == null) {
            return List.of();
        }

        return filmStorage.getFilmsLikedByUserButNotByOther(similarUserId, userId);
    }

}
