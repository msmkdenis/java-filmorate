package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorageDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ReviewStorageDaoImpl implements ReviewStorageDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Review> createReview(Review review) {
        String sqlQuery = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) " +
                          "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return findReviewById(review.getReviewId());
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        final String sqlQuery = "UPDATE REVIEWS " +
                                "SET CONTENT = ?, IS_POSITIVE = ? " +
                                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(long id) {
        final String sqlQuery = "DELETE FROM REVIEWS " +
                                "WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Optional<Review> findReviewById(long id) {
        final String sqlQuery = "SELECT * FROM REVIEWS " +
                                "WHERE REVIEW_ID = ?";
        final List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview, id);
        return reviews.size() == 0 ?
                Optional.empty() :
                Optional.ofNullable(reviews.get(0));
    }

    @Override
    public List<Review> findAllReviews() {
        final String sqlQuery = "SELECT * FROM REVIEWS";
        List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview);
        return sortReviewsByUseful(reviews);
    }

    @Override
    public List<Review> findReviewsByFilmId(long id, int count) {
        final String sqlQuery = "SELECT * FROM REVIEWS " +
                                "WHERE FILM_ID = ? " +
                                "LIMIT ?";
        List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview, id, count);
        return sortReviewsByUseful(reviews);
    }

    @Override
    public void putLikeToReview(long reviewId, long userId) {
        deleteDislikeToReview(reviewId, userId);
        final String sqlQuery = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID) " +
                                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void putDislikeToReview(long reviewId, long userId) {
        deleteLikeToReview(reviewId, userId);
        final String sqlQuery = "INSERT INTO REVIEW_DISLIKES (REVIEW_ID, USER_ID) " +
                                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void deleteLikeToReview(long reviewId, long userId) {
        final String sqlQuery = "DELETE FROM REVIEW_LIKES " +
                                "WHERE REVIEW_ID = ? " +
                                "AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void deleteDislikeToReview(long reviewId, long userId) {
        final String sqlQuery = "DELETE FROM REVIEW_DISLIKES " +
                                "WHERE REVIEW_ID = ? " +
                                "AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public List<Long> getLikesByReviewId(long reviewId) {
        final String sqlQuery = "SELECT USER_ID FROM REVIEW_LIKES " +
                                "WHERE REVIEW_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, reviewId);
    }

    @Override
    public List<Long> getDislikesByReviewId(long reviewId) {
        final String sqlQuery = "SELECT USER_ID FROM REVIEW_DISLIKES " +
                                "WHERE REVIEW_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, reviewId);
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return new Review(
                rs.getLong("REVIEW_ID"),
                rs.getString("CONTENT"),
                rs.getBoolean("IS_POSITIVE"),
                rs.getLong("USER_ID"),
                rs.getLong("FILM_ID"),
                calculateUseful(rs.getLong("REVIEW_ID")));
    }

    private Integer calculateUseful(long reviewId) {
        int likesCount = getLikesByReviewId(reviewId).size();
        int dislikesCount = getDislikesByReviewId(reviewId).size();
        return likesCount - dislikesCount;
    }

    private List<Review> sortReviewsByUseful(List<Review> reviews) {
        return reviews.stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }
}