package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ProblematicLikesException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageDao;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorageDao reviewStorageDao;
    private final UserStorageDao userStorageDao;
    private final FilmStorageDao filmStorageDao;

    public ReviewService(ReviewStorageDao reviewStorageDao, UserStorageDao userStorageDao,
                         FilmStorageDao filmStorageDao) {
        this.reviewStorageDao = reviewStorageDao;
        this.userStorageDao = userStorageDao;
        this.filmStorageDao = filmStorageDao;
    }

    public Review createReview(Review review) {
        checkUserId(review.getUserId());
        checkFilmId(review.getFilmId());
        return reviewStorageDao.createReview(review);
    }

    public Review updateReview(Review review) {
        checkReviewId(review.getReviewId());
        checkUserId(review.getUserId());
        checkFilmId(review.getFilmId());
        return reviewStorageDao.updateReview(review);
    }

    public void deleteReview(long id) {
        checkReviewId(id);
        reviewStorageDao.deleteReview(id);
    }

    public Review findReviewById(long id) {
        checkReviewId(id);
        return reviewStorageDao.findReviewById(id);
    }

    public List<Review> findAllReviews() {
        return reviewStorageDao.findAllReviews();
    }

    public List<Review> findReviewsByFilmId(long id, int count) {
        checkFilmId(id);
        return reviewStorageDao.findReviewsByFilmId(id, count);
    }

    public void putLikeToReview(long reviewId, long userId) {
        checkReviewId(reviewId);
        checkUserId(userId);
        if (reviewStorageDao.getLikesByReviewId(reviewId).contains(userId)) {
            throw new ProblematicLikesException("Пользователь не может поставить несколько лайков одному отзыву.");
        }
        reviewStorageDao.putLikeToReview(reviewId, userId);
    }

    public void putDislikeToReview(long reviewId, long userId) {
        checkReviewId(reviewId);
        checkUserId(userId);
        if (reviewStorageDao.getDislikesByReviewId(reviewId).contains(userId)) {
            throw new ProblematicLikesException("Пользователь не может поставить несколько дизлайков одному отзыву.");
        }
        reviewStorageDao.putDislikeToReview(reviewId, userId);
    }

    public void deleteLikeToReview(long reviewId, long userId) {
        checkReviewId(reviewId);
        checkUserId(userId);
        if (!reviewStorageDao.getLikesByReviewId(reviewId).contains(userId)) {
            throw new ProblematicLikesException("Пользователь не может убрать лайк, который ранее не ставил.");
        }
        reviewStorageDao.deleteLikeToReview(reviewId, userId);
    }

    public void deleteDislikeToReview(long reviewId, long userId) {
        checkReviewId(reviewId);
        checkUserId(userId);
        if (!reviewStorageDao.getDislikesByReviewId(reviewId).contains(userId)) {
            throw new ProblematicLikesException("Пользователь не может убрать дизлайк, который ранее не ставил.");
        }
        reviewStorageDao.deleteDislikeToReview(reviewId, userId);
    }

    private void checkReviewId(long id) {
//        reviewStorageDao.findReviewById(id)
//                .orElseThrow(() -> new NotFoundException(String.format("Отзыв с id = %s не найден.", id)));
        if (id <= 0 || reviewStorageDao.findAllReviews().stream().noneMatch(r -> r.getReviewId() == id)) {
            throw new NotFoundException(String.format("Отзыв с id = %s не найден.", id));
        }
    }


    private void checkUserId(long id) {
        userStorageDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", id)));
    }

    private void checkFilmId(long id) {
        filmStorageDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id = %s не найден.", id)));
    }
}