package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface BaseStorageDao<T extends BaseEntity> {

    Optional<T> findById(long id);

    List<T> findAll();

    Optional<T>  add(T entity);

    Optional<T> update(T entity);

    void deleteById(long id);
}