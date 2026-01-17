package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Validated
@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{directorId}")
    public Director getDirectorById(@PathVariable long directorId) {
        return directorService.getDirectorById(directorId);
    }

    @DeleteMapping("/{directorId}")
    public Director delete(@PathVariable Long directorId) {
        log.info("Delete director with id={}", directorId);
        return directorService.delete(directorId);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public Director create(@Valid @RequestBody Director director) {
        log.info("Create new director: {}", director);
        return directorService.create(director);
    }

    @PutMapping()
    @Validated({Marker.OnUpdate.class})
    public Director update(@Valid @RequestBody Director director) {
        log.info("Update director: {}", director);
        return directorService.update(director);
    }
}
