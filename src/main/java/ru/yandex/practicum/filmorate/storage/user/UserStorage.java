package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    List<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long id);

    User findUserById(long id);

    Map<Long, User> getUsersStorage();

    void deleteAllUser();
}
