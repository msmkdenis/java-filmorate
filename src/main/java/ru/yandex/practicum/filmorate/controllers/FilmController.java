package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@Validated
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping(value = "/films/{id}")
    public Film findFilmById(@PathVariable Long id) {
        return filmService.findFilmById(id);
    }

    @GetMapping(value = "/films")
    public List<Film> findAllFilms() {
        return filmService.findAll();
    }

    @DeleteMapping(value = "/films/{id}")
    public void deleteFilmByID(@PathVariable Long id) {
        filmService.deleteFilm(id);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id,
                        @PathVariable("userId") Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long id,
                           @PathVariable("userId") Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) @Positive Integer count,
                                       @RequestParam(defaultValue = "0") long genreId,
                                       @RequestParam(defaultValue = "0") int year) {
        return filmService.findPopularFilms(count, genreId, year);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getListFilmsDirector(@PathVariable long directorId, @RequestParam String sortBy) {
        return filmService.getListFilmsDirector(directorId, sortBy);
    }

}

