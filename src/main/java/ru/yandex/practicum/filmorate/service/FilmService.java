package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorageDao;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmService {
    private final FilmStorageDao filmStorageDao;
    private final UserService userService;
    private final LikeStorageDao likeStorageDao;
    private final GenreStorageDao genreStorageDao;
    private final DirectorStorageDao directorStorageDao;

    public FilmService(
            FilmStorageDao filmStorageDao,
            UserService userService,
            LikeStorageDao likeStorageDao,
            GenreStorageDao genreStorageDao,
            DirectorStorageDao directorStorageDao
    ) {
        this.filmStorageDao = filmStorageDao;
        this.userService = userService;
        this.likeStorageDao = likeStorageDao;
        this.genreStorageDao = genreStorageDao;
        this.directorStorageDao = directorStorageDao;
    }

    public Film addFilm(Film film) {
        filmStorageDao.add(film);
        Set<Genre> genres = genreStorageDao.findFilmGenres(film.getId());
        film.setGenres(genres);
        directorStorageDao.setFilmDirector(film);
        log.info("Добавлен фильм {}", film.getName());
        return film;
    }

    public Film findFilmById(long id) {
        Film film = filmStorageDao.findById(id).
                orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %s не найден", id)));
        Set<Genre> genres = genreStorageDao.findFilmGenres(id);
        film.setGenres(genres);
        film.setDirectors(directorStorageDao.loadFilmDirector(film));
        return film;
    }

    public Film updateFilm(Film film) {
        findFilmById(film.getId());
        Film newFilm = filmStorageDao.update(film).get();
        directorStorageDao.setFilmDirector(newFilm);
        return newFilm;
    }

    public List<Film> findAll() {
        List<Film> films = filmStorageDao.findAll();
        for (Film film : films) {
            film.setGenres(genreStorageDao.findFilmGenres(film.getId()));
            film.setDirectors(directorStorageDao.loadFilmDirector(film));
        }
        return films;
    }

    public void deleteFilm(long id) {
        findFilmById(id);
        log.info("Фильм с id = {} удалён", id);
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

    public List<Film> findPopularFilms(int count, long genreId, int year) {
        List<Film> films;
        if (genreId != 0 && year != 0) {
            films = filmStorageDao.findPopularFilmSortedByGenreAndYear(count, genreId, year);
        } else if (genreId != 0 && year == 0) {
            films = filmStorageDao.findPopularFilmSortedByGenre(count, genreId);
        } else if (genreId == 0 && year != 0) {
            films = filmStorageDao.findPopularFilmSortedByYear(count, year);
        } else {
            films = filmStorageDao.findPopularFilms(count);
        }

        for (Film film : films) {
            film.setGenres(genreStorageDao.findFilmGenres(film.getId()));
            film.setDirectors(directorStorageDao.loadFilmDirector(film));
        }
        return films;
    }

    public List<Film> getListFilmsDirector(long id, String sort) {
        if (directorStorageDao.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("Режиссер с id = %s не найден", id));
        }
        List<Film> films = filmStorageDao.getListFilmsDirector(id, sort);
        for (Film film : films) {
            film.setGenres(genreStorageDao.findFilmGenres(film.getId()));
            film.setDirectors(directorStorageDao.loadFilmDirector(film));
        }
        return films;
    }
}
