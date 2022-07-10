package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

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

    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }
}
