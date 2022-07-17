package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectUserIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{
    private final Map<Long, User> usersStorage = new HashMap<>();
    private long userID = 0;

    private long calcUserId() {
        userID++;
        return userID;
    }

    @Override
    public List<User> findAllUsers() {
        return List.copyOf(usersStorage.values());
    }

    @Override
    public User createUser(User user) {
        userGeneralValidation(user);
        long id = calcUserId();
        user.setId(id);
        usersStorage.put(id, user);
        log.info("Добавлен user: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!usersStorage.containsKey(user.getId())) {
            throw new IncorrectUserIdException("Некорректный ID пользователя");
        }
        userGeneralValidation(user);
        log.info("Обновляется старый вариант user: {}", usersStorage.get(user.getId()));
        usersStorage.put(user.getId(), user);
        log.info("Обновленный вариант user: {}", user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        if (!usersStorage.containsKey(id)) {
            throw new ValidationException("Некорректно указан id");
        }
        usersStorage.remove(id);
    }

    @Override
    public User findUserById(long id) {
        if (!usersStorage.containsKey(id)) {
            throw new IncorrectUserIdException("Некорректный ID пользователя");
        }
        return usersStorage.get(id);
    }

    @Override
    public Map<Long, User> getUsersStorage() {
        return usersStorage;
    }

    @Override
    public void deleteAllUser() {
        usersStorage.clear();
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
