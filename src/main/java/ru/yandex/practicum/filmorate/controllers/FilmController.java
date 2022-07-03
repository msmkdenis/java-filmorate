package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@Slf4j
public class FilmController {
    private final HashMap<Integer, Film> filmsStorage = new HashMap<>();
    private final ArrayList<Film> films = new ArrayList<>();
    private Integer filmId = 0;

    private Integer calcFilmId() {
        filmId++;
        return filmId;
    }

    @GetMapping("/films")
    public ArrayList<Film> findAllFilms() {
        return films;
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        filmGeneralValidation(film);
        Integer id = calcFilmId();
        film.setId(id);
        filmsStorage.put(id, film);
        films.add(film);
        log.info("Добавлен film: " + film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) {
        if (!filmsStorage.containsKey(film.getId())) {
            throw new ValidationException("Некорректно указан id у film: " + film);
        }
        filmGeneralValidation(film);
        log.info("Обновляется старый вариант film: {}", filmsStorage.get(film.getId()));
        films.remove(filmsStorage.get(filmId)); // удаляем из списка старый film по id
        filmsStorage.put(film.getId(), film);
        films.add(film);
        log.info("Обновленный вариант film: {}", film);
        return film;
    }

    void filmGeneralValidation(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Неверно указано название film: " + film);
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Превышена длина описания film: " + film);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Слишком ранняя дата релиза film: " + film);
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Отрицательная длительность film: " + film);
        }
    }
}

