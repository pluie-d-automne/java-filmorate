package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class User {
    private long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    private String birthday;
}
