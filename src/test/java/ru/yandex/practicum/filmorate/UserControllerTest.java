package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

public class UserControllerTest {
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    private final UserController userController = new UserController(userService);

    @Test
    @DisplayName("Создание user с пустой почтой")
    void createUserWithBlankMail() throws RuntimeException {
        User user = new User (
                1,
                "",
                "login",
                "name",
                LocalDate.of(1990,10,10)
        );

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        userStorage.findAllUsers();
    }

    @Test
    @DisplayName("Создание user с почтой без @")
    void createUserWithWrongMail() throws RuntimeException {
        User user = new User (
                1,
                "mail",
                "login",
                "name",
                LocalDate.of(1990,10,10)
        );

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        userStorage.findAllUsers();
    }

    @Test
    @DisplayName("Создание user с пустым логином")
    void createUserWithBlankLogin() throws RuntimeException {
        User user = new User (
                1,
                "mail@mail",
                "",
                "name",
                LocalDate.of(1990,10,10)
        );

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        userStorage.findAllUsers();
    }

    @Test
    @DisplayName("Создание user с логином с пробелами")
    void createUserWithLoginSpaces() throws RuntimeException {
        User user = new User (
                1,
                "mail@mail",
                "login login",
                "name",
                LocalDate.of(1990,10,10)
        );

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        userStorage.findAllUsers();
    }

    @Test
    @DisplayName("Создание user с некорректным днем рождения")
    void createUserWithWrongBirthday() throws RuntimeException {
        User user = new User (
                1,
                "mail@mail",
                "login",
                "name",
                LocalDate.of(2222,10,10)
        );

        Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));
        userStorage.findAllUsers();
    }

    @Test
    @DisplayName("Создание user с пустым именем")
    void createUserWithBlankName() throws RuntimeException {
        String login = "login";
        User user = new User (
                1,
                "mail@mail",
                login,
                "",
                LocalDate.of(1990,10,10)
        );
        userController.createUser(user);

        Assertions.assertEquals(user.getName(), login, "Имя не подтянулось из login");
        userStorage.findAllUsers();
    }

    @Test
    @DisplayName("Проверка общего списка друзей")
    void findCommonFriendsOfUserByUserId() throws RuntimeException {
        User user1 = new User (
                1,
                "mail@mail",
                "login",
                "name1",
                LocalDate.of(1990,10,10)
        );

        User user2 = new User (
                2,
                "mail@mail",
                "login",
                "name2",
                LocalDate.of(1990,10,10)
        );

        User user3 = new User (
                3,
                "mail@mail",
                "login",
                "name3",
                LocalDate.of(1990,10,10)
        );
        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);

        userController.addFriend(1L,2L);
        userController.addFriend(1L,3L);
        userController.addFriend(2L,3L);

        System.out.println(userController.findCommonFriendsOfUserByUserId(1L,2L));

        userStorage.findAllUsers();
    }
}
