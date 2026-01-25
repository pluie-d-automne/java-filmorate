package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String content;

    @NotNull(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private Boolean isPositive;

    @NotNull(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private Long userId;

    @NotNull(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private Long filmId;

    @NotNull(groups = Marker.OnUpdate.class)
    private Long reviewId;

    private Integer useful;
}
