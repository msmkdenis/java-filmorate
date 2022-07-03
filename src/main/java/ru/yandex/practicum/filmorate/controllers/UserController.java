package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
public class UserController {
    private final Map<Long, User> usersStorage = new HashMap<>();
    private long userID = 0;

    private long calcUserId() {
        userID++;
        return userID;
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return List.copyOf(usersStorage.values());
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        userGeneralValidation(user);
        long id = calcUserId();
        user.setId(id);
        usersStorage.put(id, user);
        log.info("Добавлен user: {}", user);
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
        if (!usersStorage.containsKey(user.getId())) {
            throw new ValidationException("Некорректно указан id у user: " + user.getId());
        }
        userGeneralValidation(user);
        log.info("Обновляется старый вариант user: {}", usersStorage.get(user.getId()));
        usersStorage.put(user.getId(), user);
        log.info("Обновленный вариант user: {}", user);
        return user;
    }

    private void userGeneralValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Неверно указана почта user: " + user);
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Неверно указан логин user: " + user);
        }
        if (LocalDate.now().isBefore(user.getBirthday())) {
            throw new ValidationException("Неверно указана дата рождения user: " + user);
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}




