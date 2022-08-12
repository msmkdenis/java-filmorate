package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.IncorrectFilmIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorageDao;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmStorageDaoImplTest {

    private final FilmStorageDao filmStorageDao;
    private final MpaStorageDao mpaStorageDao;
    private final FilmService filmService;

    @Autowired
    public FilmStorageDaoImplTest(FilmStorageDao filmStorageDao, MpaStorageDao mpaStorageDao, FilmService filmService) {
        this.filmStorageDao = filmStorageDao;
        this.mpaStorageDao = mpaStorageDao;
        this. filmService = filmService;
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

    @Test
    @DisplayName("Поиск film по его id")
    void findFilmByIdTest() {
        Film testFilm = filmStorageDao.add(createFirstFilm()).get();

        Optional<Film> filmOptional = filmStorageDao.findById(testFilm.getId());
        assertNotNull(filmOptional);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "FilmOne"));
        filmStorageDao.deleteById(testFilm.getId());
    }

    @Test
    @DisplayName("Обновление film по его id")
    void updateFilmTest() {
        Film filmBeforeUpdate = createFirstFilm();
        filmStorageDao.add(filmBeforeUpdate);

        Film filmToUpdate = createSecondFilm();
        filmToUpdate.setId(filmBeforeUpdate.getId());
        filmStorageDao.update(filmToUpdate);

        Optional<Film> filmOptional = filmStorageDao.findById(filmBeforeUpdate.getId());
        assertNotNull(filmOptional);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "FilmTwo")
                );
        filmStorageDao.deleteById(filmToUpdate.getId());
    }

    @Test
    @DisplayName("Удаление film по его id")
    void deleteFilmTest() {
        Film filmToDelete = createFirstFilm();
        filmStorageDao.add(filmToDelete);
        Long filmDeleteId = filmToDelete.getId();
        filmStorageDao.deleteById(filmDeleteId);

        assertThrows(IncorrectFilmIdException.class, () -> filmService.findFilmById(filmDeleteId));
    }
}
