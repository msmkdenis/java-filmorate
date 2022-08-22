package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface EventStorageDao {

    void addFriendEvent(long id, long friendId);

    void deleteFriendEvent(long id, long friendId);

    void addLikeEvent(long id, long userId);

    void deleteLikeEvent(long id, long userId);

    void addReviewEvent(Review reviewAnswer);

    void updateReviewEvent(Review reviewAnswer);

    void deleteReviewEvent(Review review);

    List<Event> getFeed(long userId);
}
