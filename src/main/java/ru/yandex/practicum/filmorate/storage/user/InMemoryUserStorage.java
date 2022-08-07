package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

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
        long id = calcUserId();
        user.setId(id);
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        usersStorage.put(id, user);
        log.info("Добавлен user: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Обновляется старый вариант user: {}", usersStorage.get(user.getId()));
        usersStorage.put(user.getId(), user);
        log.info("Обновленный вариант user: {}", user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        usersStorage.remove(id);
    }

    @Override
    public User findUserById(long id) {
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
}
