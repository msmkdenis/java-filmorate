package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.EventStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorageDao userStorageDao;
    private final FriendshipStorageDao friendshipStorageDao;
    private final EventStorageDao eventStorageDao;
    private final LikeStorageDao likeStorageDao;

    public User addUser(User user) {
        return userStorageDao.add(user).get();
    }

    public User updateUser(User user) {
        findUserById(user.getId());
        userStorageDao.update(user);
        return userStorageDao.update(user).get();
    }

    public User findUserById(long id) {
        return userStorageDao.findById(id).
                orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", id)));
    }

    public List<User> findAll() {
        return userStorageDao.findAll();
    }

    public void deleteUser(long id) {
        findUserById(id);
        userStorageDao.deleteById(id);
    }

    public void addFriend(long id, long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        Friendship friendship = new Friendship(user, friend);
        friendshipStorageDao.addFriend(friendship);
        eventStorageDao.addFriendEvent(id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        Friendship friendship = new Friendship(user, friend);
        friendshipStorageDao.removeFriend(friendship);
        eventStorageDao.deleteFriendEvent(id, friendId);
    }

    public List<User> findFriendsByUserID(long id) {
        findUserById(id);
        return friendshipStorageDao.findUserFriends(id);
    }

    public List<User> findMutualFriends(long id, long otherId) {
        findUserById(id);
        findUserById(otherId);
        return friendshipStorageDao.findMutualFriends(id, otherId);
    }

    public List<Event> getFeed(long userId) {
        findUserById(userId);
        return eventStorageDao.getFeed(userId);
    }
    public List<Film> filmRecommendations(Long id) {
        findUserById(id);
        return likeStorageDao.filmRecommendations(id);
    }
}
