package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorageDao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Repository
public class LikeStorageDaoImpl implements LikeStorageDao {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorageDao mpaStorageDao;

    public LikeStorageDaoImpl(JdbcTemplate jdbcTemplate, MpaStorageDao mpaStorageDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorageDao = mpaStorageDao;
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

    @Override
    public List<Film> findPopularFilms(Integer count) {
        SqlRowSet sqlQuery = jdbcTemplate.queryForRowSet(
                "SELECT * " +
                        "FROM FILMS F " +
                        "LEFT JOIN " +
                        "(SELECT FILM_ID, " +
                        "COUNT(*) LIKES_COUNT " +
                        "FROM LIKES " +
                        "GROUP BY FILM_ID) " +
                        "L ON F.FILM_ID = L.FILM_ID " +
                        "LEFT JOIN MPA ON F.MPA_ID = MPA.MPA_ID " +
                        "ORDER BY L.LIKES_COUNT DESC LIMIT ?", count);

        return findFilms(sqlQuery);
    }

    private List<Film> findFilms(SqlRowSet sqlQuery) {
        List<Film> films = new ArrayList<>();
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (sqlQuery.next()) {
            Film film = new Film(sqlQuery.getLong("FILM_ID"),
                    sqlQuery.getString("FILM_NAME"),
                    sqlQuery.getString("DESCRIPTION"),
                    LocalDate.parse(Objects.requireNonNull(sqlQuery.getString("RELEASE_DATE")), dtf),
                    sqlQuery.getInt("DURATION"),
                    mpaStorageDao.findById(sqlQuery.getInt("MPA_ID")).get());
            films.add(film);
        }
        return films;
    }
}
