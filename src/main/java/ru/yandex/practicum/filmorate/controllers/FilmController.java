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

    @GetMapping("/films/{id}")
    public Film findFilmById(@PathVariable Long id) {
        return filmService.findFilmById(id);
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilmByID(@PathVariable Long id) {
        filmService.deleteFilm(id);
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}

