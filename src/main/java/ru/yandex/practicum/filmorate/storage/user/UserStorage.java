package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long id);

    List<User> findFriends(User user);

    List<User> findCommonFriends (User user, User otherUser);

    Optional<User> findUserById(long id);

    void deleteAllUser();
}
