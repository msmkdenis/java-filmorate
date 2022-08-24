package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        User userCreate = userService.addUser(user);
        log.info("Добавлен пользователь {}", user.getName());
        return userCreate;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        User userUpdate = userService.updateUser(user);
        log.info("Обновлен пользователь {}", userUpdate.getName());
        return userUpdate;
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable long id) {
        User user = userService.findUserById(id);
        log.info("Получен пользователь {}", user.getName());
        return user;
    }

    @GetMapping
    public List<User> findAllUsers() {
        List<User> users = userService.findAll();
        log.info("Получен список всех пользователей");
        return users;
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        userService.deleteUser(id);
        log.info("Пользователь с id = {} удалён", id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriendsByUserID(@PathVariable long id) {
        List<User> users = userService.findFriendsByUserID(id);
        log.info("Получен список друзей пользователя {}", id);
        return users;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        List<User> users = userService.findMutualFriends(id, otherId);
        log.info("Получен список общих друзей пользователя {} и пользователя {}", id, otherId);
        return users;
    }

    @GetMapping("/{userId}/feed")
    public List<Event> getFeed(@PathVariable long userId) {
        List<Event> events = userService.getFeed(userId);
        log.info("Получен список событий пользователя {}", userId);
        return events;
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getFilmRecommendations(@PathVariable Long id) {
        log.info("Получены рекомендации пользователя {}", id);
        return userService.getFilmRecommendations(id);
    }
}