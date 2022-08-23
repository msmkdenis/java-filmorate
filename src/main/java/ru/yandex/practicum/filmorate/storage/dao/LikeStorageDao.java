package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Like;

public interface LikeStorageDao {

    void addLike(Like like);

    void deleteLike(Like like);
}
