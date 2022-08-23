package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
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
        Collection<Director> directors = directorService.getAll();
        log.info("Получен список всех режиссеров");
        return directors;
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable long id) {
        Director director = directorService.getById(id);
        log.info("Получен режиссер с id = {}", id);
        return director;
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        Director directorCreate = directorService.create(director);
        log.info("Режиссер с id = {} создан", director.getId());
        return directorCreate;
    }

    //Изменение режиссёра
    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        Director directorUpdate = directorService.update(director);
        log.info("Режиссер с id = {} обновлен", director.getId());
        return directorUpdate;
    }

    //Удаление режиссёра
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        directorService.delete(id);
        log.info("Режиссер с id = {} удален", id);
    }
}
