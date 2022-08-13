package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageDao;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class FriendshipStorageDaoImplTest {

    private final UserStorageDao userStorageDao;
    private final FriendshipStorageDao friendshipStorageDao;

    @Autowired
    public FriendshipStorageDaoImplTest(
            UserStorageDao userStorageDao,
            FriendshipStorageDao friendshipStorageDao)
    {
        this.userStorageDao = userStorageDao;
        this.friendshipStorageDao = friendshipStorageDao;
    }

    private User createFirstUser(){
        return new User(
                "first@email.ru",
                "firstLogin",
                "firstName",
                LocalDate.of(1950, 1, 1));
    }

    private User createSecondUser(){
        return new User(
                "second@email.ru",
                "secondLogin",
                "secondName",
                LocalDate.of(1955, 1, 1));
    }

    private User createThirdUser(){
        return new User(
                "third@email.ru",
                "thirdLogin",
                "thirdName",
                LocalDate.of(1960, 1, 1));
    }

    @Test
    @DisplayName("Добавление друга")
    void addFriendTest() {
        User firstUser = createFirstUser();
        User secondUser = createSecondUser();
        Friendship friendship = new Friendship(firstUser, secondUser);
        userStorageDao.add(firstUser);
        userStorageDao.add(secondUser);

        assertTrue(friendshipStorageDao.findUserFriends(firstUser.getId()).isEmpty());

        friendshipStorageDao.addFriend(friendship);

        assertFalse(friendshipStorageDao.findUserFriends(firstUser.getId()).isEmpty());
        assertTrue(friendshipStorageDao.findUserFriends(secondUser.getId()).isEmpty());
        assertEquals(secondUser.getId(), friendshipStorageDao.findUserFriends(firstUser.getId()).get(0).getId());

        userStorageDao.deleteById(firstUser.getId());
        userStorageDao.deleteById(secondUser.getId());
    }

    @Test
    @DisplayName("Удаление друга")
    void removeFriend() {
        User firstUser = createFirstUser();
        User secondUser = createSecondUser();
        Friendship friendship = new Friendship(firstUser, secondUser);
        userStorageDao.add(firstUser);
        userStorageDao.add(secondUser);

        assertTrue(friendshipStorageDao.findUserFriends(firstUser.getId()).isEmpty());

        friendshipStorageDao.addFriend(friendship);

        assertFalse(friendshipStorageDao.findUserFriends(firstUser.getId()).isEmpty());

        friendshipStorageDao.removeFriend(friendship);

        assertTrue(friendshipStorageDao.findUserFriends(firstUser.getId()).isEmpty());

        userStorageDao.deleteById(firstUser.getId());
        userStorageDao.deleteById(secondUser.getId());
    }

    @Test
    @DisplayName("Найти совместных друзей")
    void findMutualFriends() {
        User firstUser = createFirstUser();
        User secondUser = createSecondUser();
        User thirdUser = createThirdUser();
        Friendship friendship1_3 = new Friendship(firstUser, thirdUser);
        Friendship friendship2_3 = new Friendship(secondUser, thirdUser);
        userStorageDao.add(firstUser);
        userStorageDao.add(secondUser);
        userStorageDao.add(thirdUser);

        assertTrue(friendshipStorageDao.findMutualFriends(firstUser.getId(), secondUser.getId()).isEmpty());

        friendshipStorageDao.addFriend(friendship1_3);
        friendshipStorageDao.addFriend(friendship2_3);

        assertEquals(1, friendshipStorageDao.findMutualFriends(firstUser.getId(), secondUser.getId()).size());

        userStorageDao.deleteById(firstUser.getId());
        userStorageDao.deleteById(secondUser.getId());
        userStorageDao.deleteById(thirdUser.getId());
    }
}
