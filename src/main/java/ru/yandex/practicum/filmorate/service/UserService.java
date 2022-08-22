package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageDao;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorageDao userStorageDao;
    private final FriendshipStorageDao friendshipStorageDao;
    private final LikeStorageDao likeStorageDao;

    public UserService(UserStorageDao userStorageDao, FriendshipStorageDao friendshipStorageDao,
                       LikeStorageDao likeStorageDao) {
        this.userStorageDao = userStorageDao;
        this.friendshipStorageDao = friendshipStorageDao;
        this.likeStorageDao = likeStorageDao;
    }

    public User addUser(User user) {
        return userStorageDao.add(user).get();
    }

    public User updateUser(User user) {
        findUserById(user.getId());
        userStorageDao.update(user);
        return userStorageDao.update(user).get();
    }

    public User findUserById(Long id) {
        return userStorageDao.findById(id).
                orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", id)));
    }

    public List<User> findAll() {
        return userStorageDao.findAll();
    }

    public void deleteUser(Long id) {
        findUserById(id);
        log.info("Удаляется user {}", id);
        userStorageDao.deleteById(id);
    }

    public void addFriend(Long id, Long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        Friendship friendship = new Friendship(user, friend);
        friendshipStorageDao.addFriend(friendship);
        log.info("User {} добавил в друзья user {}", user.getName(), friend.getName());
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        Friendship friendship = new Friendship(user, friend);
        friendshipStorageDao.removeFriend(friendship);
        log.info("User {} удалил из друзей user {}", user.getName(), friend.getName());
    }

    public List<User> findFriendsByUserID(Long id) {
        findUserById(id);
        return friendshipStorageDao.findUserFriends(id);
    }

    public List<User> findMutualFriends(Long id, Long otherId) {
        findUserById(id);
        findUserById(otherId);
        return friendshipStorageDao.findMutualFriends(id, otherId);
    }
    public List<Film> filmRecommendations(Long id) {
        findUserById(id);
        return likeStorageDao.filmRecommendations(id);
    }
}
