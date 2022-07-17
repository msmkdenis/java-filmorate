package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exception.IncorrectFilmIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

public class FilmControllerTest {
    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final FilmService filmService = new FilmService(filmStorage, userStorage);
    private final FilmController filmController = new FilmController(filmService);
    private final String LONG_DESCRIPTION = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";


    @Test
    @DisplayName("Создание film с пустым именем")
    void createFilmWithBlankName() throws RuntimeException {
        Film film = new Film(
                1,
                "",
                "description",
                LocalDate.of(1990, 10, 10),
                50);

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    @DisplayName("Создание film с именем >  200 символов")
    void createFilmWithLongName() throws RuntimeException {
        Film film = new Film(
                1,
                "name",
                LONG_DESCRIPTION,
                LocalDate.of(1990, 10, 10),
                50);

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    @DisplayName("Создание film датой релиза до 1895.12.28")
    void createFilmWithEarlyRelease() throws RuntimeException {
        Film film = new Film(
                1,
                "name",
                "description",
                LocalDate.of(1800, 10, 10),
                50);

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    @DisplayName("Создание film с отрицательной длительностью")
    void createFilmWithNegativeDuration() throws RuntimeException {
        Film film = new Film(
                1,
                "name",
                "description",
                LocalDate.of(11990, 10, 10),
                -50);

        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    @DisplayName("Обновление film с некорректным id")
    void updateFilmWithWrongId() throws RuntimeException {
        Film film = new Film(
                -1,
                "name",
                "description",
                LocalDate.of(1990, 10, 10),
                50);

        Assertions.assertThrows(IncorrectFilmIdException.class, () -> filmController.updateFilm(film));
    }

    @Test
    @DisplayName("Обновление film с пустым именем")
    void updateFilmWithWrongName() throws RuntimeException {
        Film film = new Film(
                1,
                "",
                "description",
                LocalDate.of(1990, 10, 10),
                50);

        Assertions.assertThrows(IncorrectFilmIdException.class, () -> filmController.updateFilm(film));
    }

    @Test
    @DisplayName("Обновление film с именем >  200 символов")
    void updateFilmWithLongName() throws RuntimeException {
        Film film = new Film(
                1,
                "name",
                LONG_DESCRIPTION,
                LocalDate.of(1990, 10, 10),
                50);

        Assertions.assertThrows(IncorrectFilmIdException.class, () -> filmController.updateFilm(film));
    }

    @Test
    @DisplayName("Обновление film с датой релиза до 1895.12.28")
    void updateFilmWithEarlyRelease() throws RuntimeException {
        Film film = new Film(
                1,
                "name",
                "description",
                LocalDate.of(1800, 10, 10),
                50);

        Assertions.assertThrows(IncorrectFilmIdException.class, () -> filmController.updateFilm(film));
    }

    @Test
    @DisplayName("Обновление film с отрицательной длительностью")
    void updateFilmWithNegativeDuration() throws RuntimeException {
        Film film = new Film(
                1,
                "name",
                "description",
                LocalDate.of(11990, 10, 10),
                -50);

        Assertions.assertThrows(IncorrectFilmIdException.class, () -> filmController.updateFilm(film));
    }
}
