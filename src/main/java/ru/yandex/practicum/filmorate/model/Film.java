package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.CinemaDate;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Film {
    @Null(groups = Marker.OnCreate.class, message = "При добавлении нового фильма id должен быть пустым.")
    @NotNull(groups = Marker.OnUpdate.class, message = "Необходимо указать id фильма, который требуется изменить.")
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Название фильма не может быть пустым.")
    private String name;
    @Size(
            max = 200,
            groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Описание не должно превышать 200 символов."
    )
    private String description;
    @CinemaDate(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Фильм не мог выйти в указанном году.")
    private LocalDate releaseDate;
    @Positive(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Продолжительность фильма должна быть положительным числом.")
    private Integer duration;
    private Set<Long> likes;
    private Collection<Genre> genres;
    private Mpa mpa;
    private int likesCnt;
}
