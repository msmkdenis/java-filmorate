package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FriendshipStorageDao {

    void addFriend(Friendship friendship);

    void removeFriend(Friendship friendship);

    List<Long> findUserFriends(long userId);

    List<Long> findMutualFriends(long userId, long otherId);
}
