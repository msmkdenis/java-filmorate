package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorageDao {

    void addLike(Like like);

    void deleteLike(Like like);
    List<Film> filmRecommendations(Long id);
}
