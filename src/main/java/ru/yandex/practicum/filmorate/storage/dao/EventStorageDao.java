package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface EventStorageDao {

    void addFriendEvent(Long id, Long friendId);

    void deleteFriendEvent(Long id, Long friendId);

    void addLikeEvent(Long id, Long userId);

    void deleteLikeEvent(Long id, Long userId);

    void addReviewEvent(Review reviewAnswer);

    void updateReviewEvent(Review reviewAnswer);

    void deleteReviewEvent(Review review);

    List<Event> getFeed(Long userId);
}
