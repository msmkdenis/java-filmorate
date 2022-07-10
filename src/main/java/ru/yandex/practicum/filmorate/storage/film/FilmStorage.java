package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
public interface FilmStorage {

    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(long id);

    Film findFilmById(long id);

}
