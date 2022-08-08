package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectUserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        userStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) {
        findUserById(user.getId());
        userStorage.updateUser(user);
        return user;
    }

    public List<User> findAll() {
        return userStorage.findAllUsers();
    }

    public void deleteUser(Long id) {
        findUserById(id);
        userStorage.deleteUser(id);
    }

    public void addFriend(Long userId, Long friendToAddId) {
        User user = findUserById(userId);
        User friend = findUserById(friendToAddId);
        user.getFriends().add(friendToAddId);
        friend.getFriends().add(userId);
        log.info("Пользователь {} добавил в друзья пользователя {}", user.getName(), friend.getName());
    }

    public List<User> findFriendsByUserID(Long id) {
        User user = findUserById(id);
        return userStorage.findFriends(user);
    }

    public List<User> findCommonFriendsOfUserByUserId(Long id, Long otherID) {
        User user = findUserById(id);
        User otherUser = findUserById(otherID);
        return userStorage.findCommonFriends(user, otherUser);
    }

    public void deleteFriend(Long userId, Long friendToRemoveId) {
        User user = findUserById(userId);
        User friendToRemove = findUserById(friendToRemoveId);
        user.getFriends().remove(friendToRemoveId);
        friendToRemove.getFriends().remove(userId);
        log.info("Пользователь {} удалил из друзей пользователя {}", user.getName(), friendToRemove.getName());
    }

    public User findUserById(Long id) {
        return userStorage.findUserById(id).orElseThrow(() -> new IncorrectUserIdException("Некорректный ID пользователя"));
    }
}
