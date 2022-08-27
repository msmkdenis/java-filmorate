package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorageDao {

    Optional<Genre> findById(long id);

    List<Genre> findAll();

    Set<Genre> findFilmGenres(long id);
}