package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectUserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
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
        userStorage.updateUser(user);
        return user;
    }

    public List<User> findAll() {
        return userStorage.findAllUsers();
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(Long userId, Long friendToAddId) {
        if (userStorage.getUsersStorage().containsKey(userId)) {
            if (userStorage.getUsersStorage().containsKey(friendToAddId)) {
                userStorage.findUserById(userId).getFriends().add(friendToAddId);
                userStorage.findUserById(friendToAddId).getFriends().add(userId);
                log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendToAddId);
            } else {
                throw new IncorrectUserIdException("Некорректный ID пользователя");
            }
        } else {
            throw new IncorrectUserIdException("Некорректный ID пользователя");
        }
    }

    public List<User> findFriendsByUserID(Long id) {
        List<User> friends = new ArrayList<>();
        if (userStorage.getUsersStorage().containsKey(id)) {
            if (!userStorage.getUsersStorage().get(id).getFriends().isEmpty()) {
                for (long idFriend : userStorage.getUsersStorage().get(id).getFriends()) {
                    friends.add(userStorage.getUsersStorage().get(idFriend));
                } return friends;
            } return null;
        } else {
            throw new IncorrectUserIdException("Некорректный ID пользователя");
        }
    }

    public List<User> findCommonFriendsOfUserByUserId(Long id, Long otherID) {
        List<User> commonFriends = new ArrayList<>();
        Set<Long> userFriends = userStorage.getUsersStorage().get(id).getFriends();
        Set<Long> otherUserFriends = userStorage.getUsersStorage().get(otherID).getFriends();
        for (Long elem : userFriends) {
            for (Long otherElem : otherUserFriends) {
                if (elem.equals(otherElem)) {
                    commonFriends.add(userStorage.getUsersStorage().get(elem));
                }
            }
        }
        return commonFriends;
    }

    public void deleteFriend(Long userId, Long friendToAddId) {
        userStorage.findUserById(userId).getFriends().remove(friendToAddId);
        userStorage.findUserById(friendToAddId).getFriends().remove(userId);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendToAddId);
    }

    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }
}
