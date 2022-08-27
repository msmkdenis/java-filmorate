package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorageDao {

    Optional<Mpa> findById(long id);

    List<Mpa> findAll();
}