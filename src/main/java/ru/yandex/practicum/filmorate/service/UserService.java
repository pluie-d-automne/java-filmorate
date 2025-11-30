package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        Set<Long> userFriends = (user.getFriends() == null) ? new HashSet<>() : user.getFriends();
        Set<Long> friendFriends = (friend.getFriends() == null) ? new HashSet<>() : friend.getFriends();
        userFriends.add(friendId);
        friendFriends.add(userId);
        user.setFriends(userFriends);
        friend.setFriends(friendFriends);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        Set<Long> userFriends = (user.getFriends() == null) ? new HashSet<>() : user.getFriends();
        Set<Long> friendFriends = (friend.getFriends() == null) ? new HashSet<>() : friend.getFriends();
        userFriends.remove(friendId);
        friendFriends.remove(userId);
        user.setFriends(userFriends);
        friend.setFriends(friendFriends);

    }

    public Set<User> getUserFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        Set<Long> userFriends = (user.getFriends() == null) ? new HashSet<>() : user.getFriends();
        return userFriends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        Set<Long> userFriends = (user.getFriends() == null) ? new HashSet<>() : user.getFriends();
        Set<Long> otherUserFriends = (otherUser.getFriends() == null) ? new HashSet<>() : otherUser.getFriends();
        Set<Long> commonUserIds = userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toSet());
        return userStorage.getAllUsers()
                .stream()
                .filter(checkedUser -> commonUserIds.contains(checkedUser.getId()))
                .collect(Collectors.toSet());
    }
}
