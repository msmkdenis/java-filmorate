package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorageDao {

    Optional<Review> createReview(Review review);

    Optional<Review> updateReview(Review review);

    void deleteReview(long id);

    Optional<Review> findReviewById(long id);

    List<Review> findAllReviews();

    List<Review> findReviewsByFilmId(long id, int count);

    void putLikeToReview(long reviewId, long userId);

    void putDislikeToReview(long reviewId, long userId);

    void deleteLikeToReview(long reviewId, long userId);

    void deleteDislikeToReview(long reviewId, long userId);

    List<Long> getLikesByReviewId(long reviewId);

    List<Long> getDislikesByReviewId(long reviewId);
}