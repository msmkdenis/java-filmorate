package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.IncorrectGenreIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorageDao;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class GenreStorageDaoImplTest {

    private final GenreStorageDao genreStorageDao;
    private final MpaStorageDao mpaStorageDao;
    private final FilmStorageDao filmStorageDao;
    private final GenreService genreService;

    @Autowired
    public GenreStorageDaoImplTest(
            GenreStorageDao genreStorageDao,
            MpaStorageDao mpaStorageDao,
            FilmStorageDao filmStorageDao,
            GenreService genreService)
    {
        this.genreStorageDao = genreStorageDao;
        this.mpaStorageDao = mpaStorageDao;
        this.filmStorageDao = filmStorageDao;
        this.genreService = genreService;
    }

    private Film createFirstFilm() {
        Film film = new Film(
                "FilmOne",
                "FilmGetDescriptionOne",
                LocalDate.of(2020, 2, 22),
                120,
                mpaStorageDao.findById(1).get());
        return film;
    }

    @Test
    @DisplayName("Найти genre по id")
    void findByIdTest() {
        Genre genre = genreService.findById(1L);

        assertEquals("Комедия", genre.getName());
        assertThrows(IncorrectGenreIdException.class, ()-> genreService.findById(-1L));
    }

    @Test
    @DisplayName("Найти genre у film")
    void findFilmGenresTest() {
        Film filmGenre = createFirstFilm();
        Set<Genre> genres = new HashSet<>();
        genres.add(genreService.findById(1L));
        genres.add(genreService.findById(2L));
        genres.add(genreService.findById(3L));
        genres.add(genreService.findById(2L));
        genres.add(genreService.findById(1L));
        filmGenre.setGenres(genres);
        filmStorageDao.add(filmGenre);
        Set<Genre> filmGenres = genreStorageDao.findFilmGenres(filmGenre.getId());

        assertEquals(3, filmGenres.size());
    }
}
