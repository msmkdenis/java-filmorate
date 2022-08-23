package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping(value = "/{id}")
    public User findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAll();
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long id,
                          @PathVariable("friendId") Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long id,
                             @PathVariable("friendId") Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User>  findFriendsByUserID(@PathVariable("id") Long id) {
        return userService.findFriendsByUserID(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User>  findMutualFriends(@PathVariable("id") Long id,
                                     @PathVariable("otherId") Long otherId) {
        return userService.findMutualFriends(id, otherId);
    }

    @GetMapping("/{userId}/feed")
    public List<Event> getFeed(@PathVariable Long userId) {
        return userService.getFeed(userId);
    }
}




