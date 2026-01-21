package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFeedEvent;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getUserFriends(@PathVariable long userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public Collection<User> getCommonFriends(
            @PathVariable long userId,
            @PathVariable long otherUserId
    ) {
        return userService.getCommonFriends(userId, otherUserId);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User create(@Valid @RequestBody User user) {
        log.info("Create new user: {}", user);
        return userService.create(user);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public User update(@Valid @RequestBody User user) {
        log.info("Update user: {}", user);
        return userService.update(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        log.info("User with id={} adds user with id={} to friends", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable Long userId,
            @PathVariable Long friendId
    ) {
        log.info("User with id={} deletes user with id={} from friends", userId, friendId);
        userService.deleteFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Delete user with id={}", userId);
        userService.delete(userId);
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendations(@PathVariable @Positive(message = "The user's ID must be positive") Long userId) {
        log.trace("Received a recommendation request for a user with ID: {}", userId);
        return userService.getRecommendations(userId);
    }

    @GetMapping("/{userId}/feed")
    public Collection<UserFeedEvent> getUserFeed(@PathVariable long userId) {
        log.info("Получение ленты событий для пользователя с id={}", userId);
        return userService.getUserFeed(userId);
    }
}
