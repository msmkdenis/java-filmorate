package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Long, Film> filmsStorage = new HashMap<>();
    private long filmId = 0;

    private long calcFilmId() {
        filmId++;
        return filmId;
    }

    @Override
    public List<Film> findAllFilms() {
        return List.copyOf(filmsStorage.values());
    }

    @Override
    public Film addFilm(Film film) {
        long id = calcFilmId();
        film.setId(id);
        filmsStorage.put(id, film);
        log.info("Добавлен film: " + film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
            log.info("Обновляется старый вариант film: {}", filmsStorage.get(film.getId()));
            filmsStorage.put(film.getId(), film);
            log.info("Обновленный вариант film: {}", film);
            return film;
    }

    @Override
    public void deleteFilm(long id) {
        filmsStorage.remove(id);
    }

    @Override
    public void deleteAllFilms() {
        filmsStorage.clear();
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        return Optional.ofNullable(filmsStorage.get(id));
    }
}
