package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest()
public class UserControllerTests {

    @Autowired
    private UserController userController;

    @Test
    public void postEmptyUserThrowsException() {
        User newUser = new User();

        Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(newUser);
        });
    }

    @Test
    public void postEmptyUserEmailThrowsException() {
        User newUser = new User();
        newUser.setEmail("");
        newUser.setLogin("someLogin");
        newUser.setName("someName");
        newUser.setBirthday("2000-01-01");


        Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(newUser);
        });
    }

    @Test
    public void postBadUserEmailThrowsException() {
        User newUser = new User();
        newUser.setEmail("badEmail");
        newUser.setLogin("someLogin");
        newUser.setName("someName");
        newUser.setBirthday("2000-01-01");


        Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(newUser);
        });
    }

    @Test
    public void postEmptyUserLoginThrowsException() {
        User newUser = new User();
        newUser.setEmail("some@email");
        newUser.setLogin("");
        newUser.setName("someName");
        newUser.setBirthday("2000-01-01");


        Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(newUser);
        });
    }

    @Test
    public void postBadUserLoginThrowsException() {
        User newUser = new User();
        newUser.setEmail("some@email");
        newUser.setLogin("some login");
        newUser.setName("someName");
        newUser.setBirthday("2000-01-01");


        Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(newUser);
        });
    }

    @Test
    public void postEmptyUserNameThrowsNoException() {
        User newUser = new User();
        newUser.setEmail("some@email");
        newUser.setLogin("someLogin");
        newUser.setName("");
        newUser.setBirthday("2000-01-01");


        Assertions.assertDoesNotThrow(() -> {
            userController.create(newUser);
        });
    }

    @Test
    public void postFutureUserBirthdayThrowsException() {
        User newUser = new User();
        newUser.setEmail("some@email");
        newUser.setLogin("someLogin");
        newUser.setName("someName");
        newUser.setBirthday("2100-01-01");


        Assertions.assertThrows(ValidationException.class, () -> {
            userController.create(newUser);
        });
    }
}
