package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectFilmIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorageDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmService {

    private final FilmStorageDao filmStorageDao;
    private final UserService userService;
    private final LikeStorageDao likeStorageDao;
    private final GenreStorageDao genreStorageDao;

    public FilmService(
            FilmStorageDao filmStorageDao,
            UserService userService,
            LikeStorageDao likeStorageDao,
            GenreStorageDao genreStorageDao
    ) {
        this.filmStorageDao = filmStorageDao;
        this.userService = userService;
        this.likeStorageDao = likeStorageDao;
        this.genreStorageDao = genreStorageDao;
    }

    public Film addFilm(Film film) {
        filmStorageDao.add(film);
        Set<Genre> genres = genreStorageDao.findFilmGenres(film.getId());
        film.setGenres(genres);
        log.info("Добавлен film {}", film.getName());
        return film;
    }

    public Film findFilmById(long id) {
        Film film = filmStorageDao.findById(id).
                orElseThrow(() -> new IncorrectFilmIdException("Некорректно указан id"));
        Set<Genre> genres = genreStorageDao.findFilmGenres(id);
        film.setGenres(genres);
        return film;
    }

    public Film updateFilm(Film film) {
        findFilmById(film.getId());
        return filmStorageDao.update(film).get();
    }

    public List<Film> findAll() {
        List<Film> films = filmStorageDao.findAll();
        List<Film> updatedFilms = new ArrayList<>();
        for (Film film : films) {
            film.setGenres(genreStorageDao.findFilmGenres(film.getId()));
            updatedFilms.add(film);
        }
        return updatedFilms;
    }

    public void deleteFilm(long id) {
        findFilmById(id);
        log.info("Уадляется film {}", id);
        filmStorageDao.deleteById(id);
    }

    public void addLike(Long filmId, Long userId) {
        User user = userService.findUserById(userId);
        Film film = findFilmById(filmId);
        Like like = new Like(user, film);
        likeStorageDao.addLike(like);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        User user = userService.findUserById(userId);
        Film film = findFilmById(filmId);
        Like like = new Like(user, film);
        likeStorageDao.deleteLike(like);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public List<Film> findPopularFilms(Integer count) {
        return likeStorageDao.findPopularFilms(count);
    }
}
