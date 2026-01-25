package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    @NotNull(groups = Marker.OnUpdate.class, message = "Необходимо указать id режиссёра, которого требуется изменить.")
    private Long id;
    @NotBlank(message = "Имя режиссёра не может быть пустым.")
    private String name;
}
