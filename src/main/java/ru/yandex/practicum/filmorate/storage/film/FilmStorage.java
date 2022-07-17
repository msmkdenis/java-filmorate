package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(long id);

    void deleteAllFilms();

    Film findFilmById(long id);

    Map<Long, Film> getFilmStorage();

}
