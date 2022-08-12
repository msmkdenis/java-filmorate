package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.IncorrectUserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageDao;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserStorageDaoImplTest {

    private final UserStorageDao userStorageDao;
    private final UserService userService;

    @Autowired
    public UserStorageDaoImplTest(UserStorageDao userStorageDao, UserService userService) {
        this.userStorageDao = userStorageDao;
        this.userService = userService;
    }

    private User createFirstUser() {
        return new User(
                "first@email.ru",
                "firstLogin",
                "firstName",
                LocalDate.of(1950, 1, 1));
    }

    private User createSecondUser() {
        return new User(
                "second@email.ru",
                "secondLogin",
                "secondName",
                LocalDate.of(1955, 1, 1));
    }

    private User createThirdUser() {
        return new User(
                "third@email.ru",
                "thirdLogin",
                "thirdName",
                LocalDate.of(1960, 1, 1));
    }

    @Test
    @DisplayName("Поиск user по его id")
    void userFindByIdTest() {
        User testUser = userStorageDao.add(createFirstUser()).get();

        Optional<User> userOptional = userStorageDao.findById(testUser.getId());
        assertNotNull(userOptional);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "firstName"));

        assertThrows(IncorrectUserIdException.class, () -> userService.findUserById(-1L));

        userStorageDao.deleteById(testUser.getId());
    }

    @Test
    @DisplayName("Обновление user по его id")
    void updateUserTest() {
        User userBeforeUpdate = createFirstUser();
        userStorageDao.add(userBeforeUpdate);

        User userToUpdate = createSecondUser();
        userToUpdate.setId(userBeforeUpdate.getId());
        userStorageDao.update(userToUpdate);

        Optional<User> userOptional = userStorageDao.findById(userToUpdate.getId());
        assertNotNull(userOptional);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "secondName")
                );
        userStorageDao.deleteById(userToUpdate.getId());
    }

    @Test
    @DisplayName("Удаление user по его id")
    void deleteUserTest() {
        User userToDelete = createFirstUser();
        userStorageDao.add(userToDelete);
        Long userDeleteId = userToDelete.getId();
        userStorageDao.deleteById(userDeleteId);

        assertThrows(IncorrectUserIdException.class, () -> userService.findUserById(userDeleteId));
    }
}
