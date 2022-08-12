package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorageDao;

import java.util.LinkedList;
import java.util.List;

@Repository
public class LikeStorageDaoImpl implements LikeStorageDao {

    private final JdbcTemplate jdbcTemplate;

    public LikeStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    public List<Long> findPopularFilms(Integer count) {
        SqlRowSet sqlQuery = jdbcTemplate.queryForRowSet(
                "SELECT F.FILM_ID " +
                    "FROM FILMS F " +
                    "LEFT JOIN " +
                      "(SELECT FILM_ID, " +
                      "COUNT(*) LIKES_COUNT " +
                      "FROM LIKES" +
                      " GROUP BY FILM_ID) " +
                    "L ON F.FILM_ID = L.FILM_ID " +
                    "LEFT JOIN MPA ON F.MPA_ID = MPA.MPA_ID" +
                    " ORDER BY L.LIKES_COUNT DESC LIMIT ?", count);

        final List<Long> filmsId = new LinkedList<>();
        while (sqlQuery.next()) {
            Long film = sqlQuery.getLong("FILM_ID");
            filmsId.add(film);
        }
        return filmsId;
    }
}
