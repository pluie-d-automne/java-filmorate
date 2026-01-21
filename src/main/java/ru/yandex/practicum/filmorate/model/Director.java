package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Director {
    @NotNull(groups = Marker.OnUpdate.class, message = "Необходимо указать id режиссёра, которого требуется изменить.")
    private Long id;
    @NotBlank(message = "Имя режиссёра не может быть пустым.")
    private String name;
}
