package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        long lastId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++lastId;
    }

    private boolean emailValidations(String email) {
        if (email == null || email.isBlank()) {
            String message = "Электронная почта не может быть пустой";
            log.error(message);
            throw new ValidationException(message);
        }
        if (!email.contains("@")) {
            String message = "Электронная почта должна содержать символ @";
            log.error(message);
            throw new ValidationException(message);
        }
        return true;
    }

    private boolean containsWhitespaces(String str) {
        for (char ch : str.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                return true;
            }
        }
        return false;
    }

    @GetMapping
    public Collection<User> getAllFilms() {
        log.trace("Возвращен список всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {
        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || containsWhitespaces(newUser.getLogin())) {
            String message = "Логин не может быть пустым и содержать пробелы";
            log.error(message);
            throw new ValidationException(message);
        }
        emailValidations(newUser.getEmail());
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            log.info("Имя пользователя пустое - будет использован логин");
            newUser.setName(newUser.getLogin());
        }
        if (newUser.getBirthday() != null) {
            if (LocalDate.parse(newUser.getBirthday(), DateTimeFormatter.ISO_DATE).isAfter(LocalDate.now())) {
                String message = "Дата рождения не может быть в будущем";
                log.error(message);
                throw new ValidationException(message);
            }
        }
        newUser.setId(getNextId());
        log.info("Пользователю присвоен id={}", newUser.getId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь добавлен.");
        return newUser;
    }

    @PutMapping
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
        if (newUser.getBirthday() != null) {
            if (
                    LocalDate.parse(newUser.getBirthday(), DateTimeFormatter.ISO_DATE).isBefore(LocalDate.now())
                    && !newUser.getBirthday().equals(oldUser.getBirthday())
            ) {
                oldUser.setBirthday(newUser.getBirthday());
                log.info("Дата рождения пользователя изменена на {}", newUser.getBirthday());
            }
        }
        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || containsWhitespaces(newUser.getLogin())) {
            String message = "Логин не может быть пустым и содержать пробелы";
            log.error(message);
            throw new ValidationException(message);
        }
        if (!newUser.getLogin().equals(oldUser.getLogin())) {
            oldUser.setLogin(newUser.getLogin());
            log.info("Логин пользователя изменён на {}", newUser.getLogin());
        }
        if (emailValidations(newUser.getEmail()) && ! newUser.getEmail().equals(oldUser.getEmail())) {
            oldUser.setEmail(newUser.getEmail());
            log.info("Email пользователя изменён на {}", newUser.getEmail());
        }
        log.info("Пользователь обновлён.");
        return oldUser;
    }
}
