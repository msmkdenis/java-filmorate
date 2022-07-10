package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@Slf4j
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return userService.findAll();
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}




