package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllFilms() {
        log.trace("Возвращен список всех пользователей");
        return users.values();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User create(@Valid @RequestBody User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            log.info("Имя пользователя пустое - будет использован логин");
            newUser.setName(newUser.getLogin());
        }
        newUser.setId(getNextId());
        log.info("Пользователю присвоен id={}", newUser.getId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь добавлен.");
        return newUser;
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public User update(@Valid @RequestBody User newUser) {
        long userId = newUser.getId();
        if (!users.containsKey(userId)) {
            String message = "Пользователь с id=" + userId + " не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
        User oldUser = users.get(userId);
        if (newUser.getName() != null && !newUser.getName().isBlank() && !newUser.getName().equals(oldUser.getName())) {
            oldUser.setName(newUser.getName());
            log.info("Имя пользователя изменено на {}", newUser.getName());
        }
        if (newUser.getBirthday() != null && !newUser.getBirthday().equals(oldUser.getBirthday())) {
                oldUser.setBirthday(newUser.getBirthday());
                log.info("Дата рождения пользователя изменена на {}", newUser.getBirthday());
        }
        if (newUser.getLogin() != null && !newUser.getLogin().equals(oldUser.getLogin())) {
            oldUser.setLogin(newUser.getLogin());
            log.info("Логин пользователя изменён на {}", newUser.getLogin());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
            oldUser.setEmail(newUser.getEmail());
            log.info("Email пользователя изменён на {}", newUser.getEmail());
        }
        log.info("Пользователь обновлён.");
        return oldUser;
    }

    private long getNextId() {
        long lastId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++lastId;
    }
}
