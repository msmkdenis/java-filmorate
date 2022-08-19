package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.sql.SQLException;
import java.util.Collection;

@RestController
@Validated
@RequestMapping("/directors")
@Slf4j
public class DirectorController {
    private final DirectorService directorService;
    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<Director> findAll() {
        log.info("Получен список всех режиссеров");
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable long id) {
        log.info("Получен режиссер с id = {}", id);
        return directorService.getById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Режиссер с id = {} создан", director.getId());
        return directorService.create(director);
    }

    //Изменение режиссёра
    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Режиссер с id = {} обновлен", director.getId());
        return directorService.update(director);
    }

    //Удаление режиссёра
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        directorService.delete(id);
        log.info("Режиссер с id = {} удален", id);
    }
}
