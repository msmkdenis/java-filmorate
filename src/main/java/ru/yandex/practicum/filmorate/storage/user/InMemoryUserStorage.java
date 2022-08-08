package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
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
        updateUserName(user);
        usersStorage.put(id, user);
        log.info("Добавлен user: {}", user);
        return user;
    }

    private void updateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User updateUser(User user) {
        log.info("Обновляется старый вариант user: {}", usersStorage.get(user.getId()));
        updateUserName(user);
        usersStorage.put(user.getId(), user);
        log.info("Обновленный вариант user: {}", user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        usersStorage.remove(id);
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(usersStorage.get(id));
    }

    @Override
    public List<User> findFriends(User user) {
        List<User> friends = new ArrayList<>();
        if (!user.getFriends().isEmpty()) {
            for (long idFriend : usersStorage.get(user.getId()).getFriends()) {
                friends.add(usersStorage.get(idFriend));
            }
            return friends;
        }
        return null;
    }

    @Override
    public List<User> findCommonFriends(User user, User otherUser) {
        List<User> commonFriends = new ArrayList<>();
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        for (Long elem : userFriends) {
            for (Long otherElem : otherUserFriends) {
                if (elem.equals(otherElem)) {
                    commonFriends.add(usersStorage.get(elem));
                }
            }
        }
        return commonFriends;
    }

    @Override
    public void deleteAllUser() {
        usersStorage.clear();
    }
}
