package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.NoWhitespaces;

import java.time.LocalDate;
import java.util.Set;


@Data
public class User {
    @Null(groups = Marker.OnCreate.class, message = "При добавлении нового пользователя id должен быть пустым.")
    @NotNull(groups = Marker.OnUpdate.class, message = "Необходимо указать id пользователя, которого требуется изменить.")
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Электронная почта не может быть пустой.")
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Электронная почта указана некорректно.")
    private String email;
    @NotBlank(groups = Marker.OnCreate.class, message = "Логин не может быть пустым.")
    @NoWhitespaces(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Логин не может содержать пробелы.")
    private String login;
    private String name;
    @PastOrPresent(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
    private Set<Long> friends;
}
