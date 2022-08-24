package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorageDao {

    void addFriend(Friendship friendship);

    void removeFriend(Friendship friendship);

    List<User> findUserFriends(long userId);

    List<User> findMutualFriends(long userId, long otherId);
}