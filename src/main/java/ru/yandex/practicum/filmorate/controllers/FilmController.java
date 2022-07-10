package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        return filmService.findAll();
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }
}

