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
    private final HashMap<Integer, User> usersStorage = new HashMap<>();
    private final ArrayList<User> users = new ArrayList<>(); // позволит в дальнейшем получать список по запросу
    private Integer userID = 0;

    private Integer calcUserId() {
        userID++;
        return userID;
    }

    @GetMapping("/users")
    public ArrayList<User> findAllUsers() {
        return users;
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        userGeneralValidation(user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        Integer id = calcUserId();
        user.setId(id);
        usersStorage.put(id, user);
        users.add(user);
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
        users.remove(usersStorage.get(userID)); // удаляем из списка старый user по id
        usersStorage.put(user.getId(), user);
        users.add(user);
        log.info("Обновленный вариант user: {}", user);
        return user;
    }

    void userGeneralValidation(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Неверно указана почта user: " + user);
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Неверно указан логин user: " + user);
        }
        if (LocalDate.now().isBefore(user.getBirthday())) {
            throw new ValidationException("Неверно указана дата рождения user: " + user);
        }
    }

}




