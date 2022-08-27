package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorageDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class DirectorStorageDaoImpl implements DirectorStorageDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Director> findById(long id) {
        final String sql = "SELECT DIRECTOR_ID, NAME FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        List<Director> res = jdbcTemplate.query(sql, this::makeDirector, id);
        return res.size() == 0 ?
                Optional.empty() :
                Optional.of(res.get(0));
    }

    @Override
    public List<Director> findAll() {
        final String sql = "SELECT DIRECTOR_ID, NAME FROM DIRECTORS";
        return jdbcTemplate.query(sql, this::makeDirector);
    }

    @Override
    public Optional<Director> add(Director director) {
        final String sqlQuery = "INSERT INTO DIRECTORS(NAME) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"DIRECTOR_ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return Optional.of(director);
    }

    @Override
    public Optional<Director> update(Director director) {
        final String sql = "UPDATE DIRECTORS SET NAME = ? WHERE DIRECTOR_ID = ?";
        return jdbcTemplate.update(sql, director.getName(), director.getId()) == 0 ?
                Optional.empty() :
                Optional.of(director);
    }

    @Override
    public void deleteById(long id) {
        final String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void setFilmDirector(Film film) {
        if (film.getDirectors() == null) {
            return;
        }
        for (Director director : film.getDirectors()) {
            final String sql = "INSERT INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) " +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sql, film.getId(), director.getId());
        }
    }

    @Override
    public List<Director> loadFilmDirector(Film film) {
        final String sql = "SELECT D.DIRECTOR_ID, D.NAME FROM FILMS_DIRECTORS FD " +
                "JOIN DIRECTORS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE FD.FILM_ID = ?";
        return jdbcTemplate.query(sql, this::makeDirector, film.getId());
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getLong("DIRECTOR_ID"), rs.getString("NAME"));
    }
}