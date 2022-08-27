package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorageDao filmStorageDao;
    private final UserService userService;
    private final LikeStorageDao likeStorageDao;
    private final GenreStorageDao genreStorageDao;
    private final DirectorStorageDao directorStorageDao;
    private final EventStorageDao eventStorageDao;

    public Film addFilm(Film film) {
        filmStorageDao.add(film);
        Set<Genre> genres = genreStorageDao.findFilmGenres(film.getId());
        film.setGenres(genres);
        directorStorageDao.setFilmDirector(film);
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
        return setGenresAndDirectorsForFilms(films);
    }

    public void deleteFilm(long id) {
        findFilmById(id);
        filmStorageDao.deleteById(id);
    }

    public void addLike(long filmId, long userId) {
        User user = userService.findUserById(userId);
        Film film = findFilmById(filmId);
        Like like = new Like(user, film);
        eventStorageDao.addLikeEvent(filmId, userId);
        if (likeStorageDao.getLikes(like).size() == 0) {
            likeStorageDao.addLike(like);
        }
    }

    public void deleteLike(long filmId, long userId) {
        User user = userService.findUserById(userId);
        Film film = findFilmById(filmId);
        Like like = new Like(user, film);
        likeStorageDao.deleteLike(like);
        eventStorageDao.deleteLikeEvent(filmId, userId);
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
        return setGenresAndDirectorsForFilms(films);
    }

    public List<Film> getListFilmsDirector(long id, String sort) {
        if (directorStorageDao.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("Режиссер с id = %s не найден", id));
        }
        List<Film> films = filmStorageDao.getListFilmsDirector(id, sort);
        return setGenresAndDirectorsForFilms(films);
    }

    public List<Film> findMutualFilms(long userId, long friendId) {
        userService.findUserById(userId);
        userService.findUserById(friendId);
        List<Film> films = filmStorageDao.findMutualFilms(userId, friendId);
        return setGenresAndDirectorsForFilms(films);
    }

    public List<Film> searchFilms(String query, String by) {
        List<Film> films;
        if (query == null || by == null) {
            films = filmStorageDao.findPopularFilms(findAll().size());
        } else if (by.equals("director")) {
            films = filmStorageDao.searchFilmsByDirector(query);
        } else if (by.equals("title")) {
            films = filmStorageDao.searchFilmsByTitle(query);
        } else if (by.equals("director,title") || by.equals("title,director")) {
            films = filmStorageDao.searchFilmsByTitleAndDirector(query);
        } else {
            return null;
        }
        return setGenresAndDirectorsForFilms(films);
    }

    private List<Film> setGenresAndDirectorsForFilms(List<Film> films) {
        return films.stream()
                .peek(f -> f.setGenres(genreStorageDao.findFilmGenres(f.getId())))
                .peek(f -> f.setDirectors(directorStorageDao.loadFilmDirector(f)))
                .collect(Collectors.toList());
    }
}