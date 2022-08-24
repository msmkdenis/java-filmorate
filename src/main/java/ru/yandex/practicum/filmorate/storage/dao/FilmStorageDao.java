package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorageDao extends BaseStorageDao<Film> {

    List<Film> getListFilmsDirector(long id, String sort);

    List<Film> findPopularFilms(int count);

    List<Film> findPopularFilmSortedByGenreAndYear(int count, long genreId, int year);

    List<Film> findPopularFilmSortedByGenre(int count, long genreId);

    List<Film> findPopularFilmSortedByYear(int count, int year);

    List<Film> findMutualFilms(long userId, long friendId);

    List<Film> searchFilmsByTitle(String query);

    List<Film> searchFilmsByDirector(String query);

    List<Film> searchFilmsByTitleAndDirector(String query);
}