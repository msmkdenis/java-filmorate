package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectFilmIdException;
import ru.yandex.practicum.filmorate.exception.IncorrectUserIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        if (filmStorage.getFilmStorage().containsKey(filmId)) {
            if (userStorage.getUsersStorage().containsKey(userId)) {
                filmStorage.getFilmStorage().get(filmId).getUsersLikes().add(userId);
                filmStorage.getFilmStorage().get(filmId).setLikes(filmStorage.getFilmStorage().get(filmId).getUsersLikes().size());
                log.info("Пользователь {} поставил лайк фильму {}",
                        userStorage.getUsersStorage().get(userId),
                        filmStorage.getFilmStorage().get(filmId));
            } else {
                throw new IncorrectUserIdException("Некорректный ID пользователя");
            }
        } else {
            throw new IncorrectFilmIdException("Некорректный ID фильма");
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmStorage.getFilmStorage().containsKey(filmId)) {
            if ( userStorage.getUsersStorage().containsKey(userId)) {
                filmStorage.getFilmStorage().get(filmId).getUsersLikes().remove(userId);
                filmStorage.getFilmStorage().get(filmId).setLikes(filmStorage.getFilmStorage().get(filmId).getUsersLikes().size());
                log.info("Пользователь {} удалил лайк у фильма {}",
                        userStorage.getUsersStorage().get(userId),
                        filmStorage.getFilmStorage().get(filmId));
            } else {
                throw new IncorrectUserIdException("Некорректный ID пользователя");
            }
        } else {
            throw new IncorrectFilmIdException("Некорректный ID фильма");
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAllFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .distinct()
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilmStorage().containsKey(film.getId())) {
            filmStorage.updateFilm(film);
            return film;
        } else {
            throw new IncorrectFilmIdException("Некорректно указан id у film: " + film);
        }
    }

    public void deleteFilm(long id) {
        if (filmStorage.getFilmStorage().containsKey(id)) {
            filmStorage.deleteFilm(id);
        } else {
            throw new IncorrectFilmIdException("Некорректно указан id" + id);
        }
    }

    public List<Film> findAll() {
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(long id) {
        Film film = filmStorage.findFilmById(id);
        return Optional.ofNullable(film).orElseThrow(() -> new IncorrectFilmIdException("Некорректно указан id"));
    }
}
