package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTest {
    private final UserController userController = new UserController();

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
    }


}
