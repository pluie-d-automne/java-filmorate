package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class Film {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private String releaseDate;
    @Positive
    private Integer duration;
}
