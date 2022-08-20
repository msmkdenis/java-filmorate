package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorageDao extends BaseStorageDao<Film> {
    List<Film> getListFilmsDirector(long id, String sort);
    List<Film> findMutualFilms(long userId, long friendId);
}
