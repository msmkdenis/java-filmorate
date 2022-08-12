package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static java.util.Objects.isNull;

@Repository
@Slf4j
public class FilmStorageDaoImpl implements FilmStorageDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> add(Film film) {
        String sqlQuery = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                          "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            final LocalDate releaseDate = film.getReleaseDate();
            stmt.setDate(3, Date.valueOf(releaseDate));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (isNull(film.getGenres()) || film.getGenres().isEmpty()) {
            return findById(keyHolder.getKey().longValue());
        } else {
            for (Genre genre : film.getGenres()) {
                String sql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(sql,
                        film.getId(),
                        genre.getId());
            }
        }
        return findById(keyHolder.getKey().longValue());
    }


    @Override
    public Optional<Film> findById(long id) {
        final String sqlQuery =
                "SELECT * FROM FILMS " +
                "JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                "WHERE FILM_ID = ?";
        final List<Film> films = jdbcTemplate.query(sqlQuery, this::makeLocalFilm, id);
        return films.size() == 0 ?
                Optional.empty() :
                Optional.ofNullable(films.get(0));
    }

    private Film makeLocalFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(rs.getLong("FILM_ID"),
                rs.getString("FILM_NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getInt("DURATION"),
                new Mpa(rs.getInt("MPA.MPA_ID"), rs.getString("MPA.MPA_NAME")));
    }

    @Override
    public List<Film> findAll() {
        final String sqlQuery =
                "SELECT * FROM FILMS " +
                "JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID";
        final List<Film> films = jdbcTemplate.query(sqlQuery, this::makeLocalFilm);
        return films;
    }

    @Override
    public Optional<Film> update(Film film) {
        final String sqlQuery =
                "UPDATE FILMS " +
                "SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        final String deleteGenres = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteGenres, film.getId());

        if (isNull(film.getGenres()) || film.getGenres().isEmpty()) {
            return Optional.of(film);
        } else {
            Set<Genre> genres;
            genres = film.getGenres();
            film.setGenres(genres);
            for (Genre genre : genres) {
                String sql2 = "MERGE INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(sql2,
                        film.getId(),
                        genre.getId());
            }
        }
        log.info("Обновлен film {}", film.getId());
        return Optional.of(film);
    }

    @Override
    public void deleteById(long id) {
        final String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }
}
