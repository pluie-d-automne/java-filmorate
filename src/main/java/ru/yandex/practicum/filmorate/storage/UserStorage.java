package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User newUser);

    User update(User newUser);

    User delete(Long userId);

    Collection<User> getAllUsers();

    User getUserById(Long userId);
}
