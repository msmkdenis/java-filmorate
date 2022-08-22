package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.dao.DirectorStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorageDao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
public class LikeStorageDaoImpl implements LikeStorageDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorageDao filmStorageDao;
    private final GenreStorageDao genreStorageDao;
    private final DirectorStorageDao directorStorageDao;

    public LikeStorageDaoImpl(JdbcTemplate jdbcTemplate, FilmStorageDao filmStorageDao, GenreStorageDao genreStorageDao, DirectorStorageDao directorStorageDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorageDao = filmStorageDao;
        this.genreStorageDao = genreStorageDao;
        this.directorStorageDao = directorStorageDao;
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

        List<Long> filmId = new LinkedList<>();
        while (sqlQuery.next()) {
            Long id = sqlQuery.getLong("FILM_ID");
            filmId.add(id);
        }
        return filmsWithGenres(filmId);
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

    @Override
    public List<Film> filmRecommendations(Long id) {
        List<Film> recommendations = new ArrayList<>();
        String sqlQuery = "SELECT L2.USER_ID " +
                "FROM LIKES AS L1 " +
                "JOIN LIKES AS L2 " +
                "ON L1.FILM_ID = L2.FILM_ID " +
                "WHERE L1.USER_ID<>L2.USER_ID AND L1.USER_ID = ? " +
                "GROUP BY L2.USER_ID " +
                "ORDER BY COUNT(L2.USER_ID) DESC " +
                "LIMIT 1";

        List<Long> sameLikesUser = jdbcTemplate.queryForList(sqlQuery, Long.class, id);
        if (sameLikesUser.size()!=1) {
            return recommendations;
        }
        Long sameLikesUserId = sameLikesUser.get(0);

        String sqlQuery2 = "SELECT FILM_ID FROM LIKES WHERE USER_ID = ? " +
                   "EXCEPT (SELECT FILM_ID FROM LIKES WHERE USER_ID = ?)";

        List<Long> filmDifferences = jdbcTemplate.queryForList(sqlQuery2, Long.class, sameLikesUserId, id);
        for(Long filmId: filmDifferences) {
            Film film = filmStorageDao.findById(filmId).get();
            film.setGenres(genreStorageDao.findFilmGenres(filmId));
            film.setDirectors(directorStorageDao.loadFilmDirector(film));
            recommendations.add(film);
        }
        return recommendations;
    }
}
