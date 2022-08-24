package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeStorageDaoImpl implements LikeStorageDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorageDao filmStorageDao;
    private final GenreStorageDao genreStorageDao;
    private final DirectorStorageDao directorStorageDao;
    private final UserStorageDao userStorageDao;

    @Override
    public void addLike(Like like) {
        final String sqlQuery = "INSERT INTO FILMS_LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery,
                like.getUser().getId(),
                like.getFilm().getId());
    }

    @Override
    public List<Like> getLikes(Like like) {
        String sqlQuery =
                "SELECT FILMS_LIKES.USER_ID, FILMS_LIKES.FILM_ID " +
                        "FROM FILMS_LIKES " +
                        "WHERE FILMS_LIKES.USER_ID = ? AND FILMS_LIKES.FILM_ID = ? " +
                        "LIMIT 1";
        return jdbcTemplate.query(sqlQuery, this::makeLocalLike, like.getUser().getId(), like.getFilm().getId());
    }

    @Override
    public void deleteLike(Like like) {
        final String sqlQuery = "DELETE FROM FILMS_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, like.getUser().getId(), like.getFilm().getId());
    }

    @Override
    public List<Film> filmRecommendations(Long id) {
        List<Film> recommendations = new ArrayList<>();
        String sqlQuery = "SELECT L2.USER_ID " +
                "FROM FILMS_LIKES AS L1 " +
                "JOIN FILMS_LIKES AS L2 " +
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

        String sqlQuery2 = "SELECT FILM_ID FROM FILMS_LIKES WHERE USER_ID = ? " +
                "EXCEPT (SELECT FILM_ID FROM FILMS_LIKES WHERE USER_ID = ?)";

        List<Long> filmDifferences = jdbcTemplate.queryForList(sqlQuery2, Long.class, sameLikesUserId, id);
        for(Long filmId: filmDifferences) {
            Film film = filmStorageDao.findById(filmId).get();
            film.setGenres(genreStorageDao.findFilmGenres(filmId));
            film.setDirectors(directorStorageDao.loadFilmDirector(film));
            recommendations.add(film);
        }
        return recommendations;
    }

    private Like makeLocalLike(ResultSet rs, int num) throws SQLException {
        return new Like(
                userStorageDao.findById(rs.getLong("FILMS_LIKES.USER_ID")).get(),
                filmStorageDao.findById(rs.getLong("FILMS_LIKES.FILM_ID")).get());
    }
}
