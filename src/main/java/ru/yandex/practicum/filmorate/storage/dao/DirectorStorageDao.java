package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorageDao extends BaseStorageDao<Director> {
    void setFilmDirector(Film film);
    List<Director> loadFilmDirector(Film film);
}
