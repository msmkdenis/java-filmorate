package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageDao;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
public class LikeStorageDaoImplTest {

    private final UserStorageDao userStorageDao;
    private final FilmStorageDao filmStorageDao;
    private final LikeStorageDao likeStorageDao;
    private final MpaStorageDao mpaStorageDao;

    @Autowired
    public LikeStorageDaoImplTest(
            UserStorageDao userStorageDao,
            FilmStorageDao filmStorageDao,
            LikeStorageDao likeStorageDao,
            MpaStorageDao mpaStorageDao) {
        this.userStorageDao = userStorageDao;
        this.filmStorageDao = filmStorageDao;
        this.likeStorageDao = likeStorageDao;
        this.mpaStorageDao = mpaStorageDao;
    }

    private Film createFirstFilm() {
        return new Film(
                "FilmOne",
                "FilmGetDescriptionOne",
                LocalDate.of(2020, 2, 22),
                120,
                mpaStorageDao.findById(1).get());
    }

    private Film createSecondFilm() {
        return new Film(
                "FilmTwo",
                "FilmGetDescriptionTwo",
                LocalDate.of(2021, 2, 22),
                120,
                mpaStorageDao.findById(2).get());
    }

    private User createFirstUser() {
        return new User(
                "first@email.ru",
                "firstLogin",
                "firstName",
                LocalDate.of(1950, 1, 1));
    }

    @Test
    @DisplayName("Добавление like")
    void addLikeTest() {
        Film firstFilm = createFirstFilm();
        Film secondFilm = createSecondFilm();

        filmStorageDao.add(firstFilm);
        filmStorageDao.add(secondFilm);

        User userAddLike = createFirstUser();
        Like like = new Like(userAddLike, secondFilm);
        userStorageDao.add(userAddLike);

        likeStorageDao.addLike(like);

        assertEquals(2, filmStorageDao.findPopularFilms(5).size());
        assertEquals(secondFilm.getId(), filmStorageDao.findPopularFilms(5).get(0).getId());
        userStorageDao.deleteById(userAddLike.getId());
    }

    @Test
    @DisplayName("Получение пуплярных film")
    void findPopularFilmsTest() {
        Film firstFilm = createFirstFilm();
        Film secondFilm = createSecondFilm();
        filmStorageDao.add(firstFilm);
        filmStorageDao.add(secondFilm);

        assertFalse(filmStorageDao.findPopularFilms(10).isEmpty());
        filmStorageDao.deleteById(firstFilm.getId());
        filmStorageDao.deleteById(secondFilm.getId());
    }
}
