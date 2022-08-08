package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectFilmIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = findFilmById(filmId);
        User user = userService.findUserById(userId);
        film.getUsersLikes().add(userId);
        film.setLikes(film.getUsersLikes().size());
        log.info("Пользователь {} поставил лайк фильму {}", user, film);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = findFilmById(filmId);
        User user = userService.findUserById(userId);
        film.getUsersLikes().remove(userId);
        film.setLikes(film.getUsersLikes().size());
        log.info("Пользователь {} удалил лайк у фильма {}", user, film);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .distinct()
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film updateFilm(Film film) {
        findFilmById(film.getId());
        filmStorage.updateFilm(film);
        return film;
    }

    public void deleteFilm(long id) {
        findFilmById(id);
        filmStorage.deleteFilm(id);
    }

    public List<Film> findAll() {
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id).orElseThrow(() -> new IncorrectFilmIdException("Некорректно указан id"));
    }
}
