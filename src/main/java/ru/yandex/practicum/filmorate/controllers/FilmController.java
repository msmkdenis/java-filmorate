package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@Slf4j
@Validated
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        Film filmCreate = filmService.addFilm(film);
        log.info("Добавлен фильм {}", film.getName());
        return filmCreate;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Film filmUpdate = filmService.updateFilm(film);
        log.info("Обновлен фильм {}", film.getName());
        return filmUpdate;
    }

    @GetMapping(value = "/{id}")
    public Film findFilmById(@PathVariable long id) {
        Film film = filmService.findFilmById(id);
        log.info("Получен фильм {}", film.getName());
        return film;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        List<Film> films = filmService.findAll();
        log.info("Получен список всех фильмов");
        return films;
    }

    @DeleteMapping(value = "/{id}")
    public void deleteFilmByID(@PathVariable Long id) {
        filmService.deleteFilm(id);
        log.info("Фильм с id = {} удалён", id);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long id,
                        @PathVariable("userId") long userId) {
        filmService.addLike(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long id,
                           @PathVariable("userId") long userId) {
        filmService.deleteLike(id, userId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, id);
    }

    @GetMapping(value = "/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") @Positive int count,
            @RequestParam(defaultValue = "0") long genreId, @RequestParam(defaultValue = "0") int year) {
        List<Film> films = filmService.findPopularFilms(count, genreId, year);
        log.info("Получен список популярных фильмов");
        return films;
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getListFilmsDirector(@PathVariable long directorId, @RequestParam String sortBy) {
        List<Film> films = filmService.getListFilmsDirector(directorId, sortBy);
        log.info("Получен список фильмов режиссера {}", directorId);
        return films;
    }

    @GetMapping(value = "/common")
    public List<Film> findMutualFilms(@RequestParam long userId, @RequestParam long friendId) {
        List<Film> films = filmService.findMutualFilms(userId, friendId);
        log.info("Получен список общих фильмов пользователя: {} и пользователя: {}", userId, friendId);
        return films;
    }

    @GetMapping(value = "/films/common")
    public List<Film> findMutualFilms(@RequestParam("userId") Long userId,
                                      @RequestParam("friendId") Long friendId) {
        return filmService.findMutualFilms(userId, friendId);
    }
}

