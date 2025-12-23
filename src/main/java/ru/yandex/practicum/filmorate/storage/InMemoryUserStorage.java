package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        log.trace("Возвращен список всех пользователей");
        return users.values();
    }

    @Override
    public User getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            String message = "Пользователь с id=" + userId + " не найден.";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public User create(User newUser) {
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

    @Override
    public User update(User newUser) {
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

    @Override
    public User delete(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        User deletedUser = users.get(userId);
        users.remove(userId);
        log.info("Пользователь с id={} удалён.", userId);
        return deletedUser;
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
