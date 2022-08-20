package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorageDao;

import java.util.*;

@Repository
public class LikeStorageDaoImpl implements LikeStorageDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorageDao filmStorageDao;
    private final GenreStorageDao genreStorageDao;

    public LikeStorageDaoImpl(JdbcTemplate jdbcTemplate, FilmStorageDao filmStorageDao, GenreStorageDao genreStorageDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorageDao = filmStorageDao;
        this.genreStorageDao = genreStorageDao;
    }

    @Override
    public void addLike(Like like) {
        final String sqlQuery = "INSERT INTO LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery,
                like.getUser().getId(),
                like.getFilm().getId());
    }

    @Override
    public void deleteLike(Like like) {
        final String sqlQuery = "DELETE FROM LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, like.getUser().getId(), like.getFilm().getId());
    }

    private List<Film> filmsWithGenres(List<Long> filmId) {
        List<Film> films = new ArrayList<>();
        for (Long id : filmId) {
            Film film = filmStorageDao.findById(id).get();
            film.setGenres(genreStorageDao.findFilmGenres(id));
            films.add(film);
        }
        return films;
    }
}
