package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorageDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GenreStorageDaoImpl implements GenreStorageDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> findById(long id) {
        final String sqlQuery = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        final List<Genre> genres = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeLocalGenre(rs), id);
        return genres.size() == 0 ?
                Optional.empty() :
                Optional.ofNullable(genres.get(0));
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT * FROM GENRES ORDER BY GENRE_ID";
        return new LinkedList<>(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeLocalGenre(rs)));
    }

    @Override
    public Set<Genre> findFilmGenres(long id) {
        final String sqlQuery =
                "SELECT * FROM GENRES " +
                "WHERE GENRE_ID IN " +
                  "(SELECT FILM_GENRES.GENRE_ID " +
                  "FROM FILM_GENRES " +
                  "WHERE FILM_ID = ?) " +
                "ORDER BY GENRE_ID";
        final Set<Genre> genres = new LinkedHashSet<>();
        genres.addAll(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeLocalGenre(rs), id));
        return genres;
    }

    private Genre makeLocalGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getLong("GENRE_ID"),
                rs.getString("GENRE_NAME")
        );
    }
}