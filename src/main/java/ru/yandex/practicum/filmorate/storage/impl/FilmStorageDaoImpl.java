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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
        filmWithGenres(film).get();
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

        deleteGenresFromFilm(film);

        final String sqlDelete = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlDelete, film.getId());
        return filmWithGenres(film);
    }

    @Override
    public void deleteById(long id) {
        final String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private void deleteGenresFromFilm(Film film) {
        final String deleteGenres = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteGenres, film.getId());
    }

    private Optional<Film> filmWithGenres(Film film) {
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

    public List<Film> getListFilmsDirector(long id, String sort) {
        List<Film> films = null;
        switch (sort) {
            case "year":
                final String sql1 = "SELECT F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                        "F.DURATION, M.MPA_ID, M.MPA_NAME, FD.DIRECTOR_ID " +
                        "FROM FILMS F " +
                        "JOIN MPA M ON F.MPA_ID = M.MPA_ID " +
                        "JOIN FILMS_DIRECTORS FD ON F.FILM_ID = FD.FILM_ID " +
                        "WHERE FD.DIRECTOR_ID = ? " +
                        "ORDER BY F.RELEASE_DATE";
                films = jdbcTemplate.query(sql1, this::makeLocalFilm, id);
                break;
            case "likes":
                final String sql2 = "SELECT F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                        "F.DURATION, M.MPA_ID, M.MPA_NAME, FD.DIRECTOR_ID " +
                        "FROM FILMS F " +
                        "JOIN MPA M ON F.MPA_ID = M.MPA_ID " +
                        "LEFT JOIN FILMS_LIKES L ON F.FILM_ID = L.FILM_ID " +
                        "JOIN FILMS_DIRECTORS FD ON F.FILM_ID = FD.FILM_ID " +
                        "WHERE FD.DIRECTOR_ID = ? " +
                        "GROUP BY F.FILM_ID " +
                        "ORDER BY COUNT(L.USER_ID)";
                films = jdbcTemplate.query(sql2, this::makeLocalFilm, id);
                break;
        }
        return films;
    }

    private Film makeLocalFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(rs.getLong("FILM_ID"),
                rs.getString("FILM_NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getInt("DURATION"),
                new Mpa(rs.getInt("MPA.MPA_ID"), rs.getString("MPA.MPA_NAME")));
    }
}
