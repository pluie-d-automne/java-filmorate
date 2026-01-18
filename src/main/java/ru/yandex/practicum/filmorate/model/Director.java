package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class Director {
    @Null(groups = Marker.OnCreate.class, message = "При добавлении нового режиссёра id должен быть пустым.")
    @NotNull(groups = Marker.OnUpdate.class, message = "Необходимо указать id режиссёра, которого требуется изменить.")
    private Long id;
    @NotBlank(message = "Имя режиссёра не может быть пустым.")
    private String name;
}
