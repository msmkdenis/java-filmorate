package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Long, Film> filmsStorage = new HashMap<>();
    private long filmId = 0;
    private final LocalDate EARLIEST_RELEASE = LocalDate.of(1895, 12, 28);

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
        filmGeneralValidation(film);
        long id = calcFilmId();
        film.setId(id);
        filmsStorage.put(id, film);
        log.info("Добавлен film: " + film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!filmsStorage.containsKey(film.getId())) {
            throw new ValidationException("Некорректно указан id у film: " + film);
        }
        filmGeneralValidation(film);
        log.info("Обновляется старый вариант film: {}", filmsStorage.get(film.getId()));
        filmsStorage.put(film.getId(), film);
        log.info("Обновленный вариант film: {}", film);
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        if (!filmsStorage.containsKey(id)) {
            throw new ValidationException("Некорректно указан id");
        }
        filmsStorage.remove(id);
    }

    @Override
    public Film findFilmById(long id) {
        if (!filmsStorage.containsKey(id)) {
            throw new ValidationException("Некорректно указан id");
        }
        return filmsStorage.get(id);
    }

    private void filmGeneralValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Неверно указано название film: " + film);
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Превышена длина описания film: " + film);
        }
        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE)) {
            throw new ValidationException("Слишком ранняя дата релиза film: " + film);
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Отрицательная длительность film: " + film);
        }
    }
}
