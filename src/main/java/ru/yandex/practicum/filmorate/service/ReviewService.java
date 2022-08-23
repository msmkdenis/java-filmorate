package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ProblematicLikesException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.EventStorageDao;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorageDao;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorageDao reviewStorageDao;
    private final UserService userService;
    private final FilmService filmService;
    private final EventStorageDao eventStorageDao;

    public ReviewService(
            ReviewStorageDao reviewStorageDao,
            UserService userService,
            FilmService filmService,
            EventStorageDao eventStorageDao)
    {
        this.reviewStorageDao = reviewStorageDao;
        this.userService = userService;
        this.filmService = filmService;
        this.eventStorageDao = eventStorageDao;
    }

    public Review createReview(Review review) {
        userService.findUserById(review.getUserId());
        filmService.findFilmById(review.getFilmId());
        Review createdReview = reviewStorageDao.createReview(review).get();
        eventStorageDao.addReviewEvent(createdReview);
        return createdReview;
    }

    public Review updateReview(Review review) {
        findReviewById(review.getReviewId());
        userService.findUserById(review.getUserId());
        filmService.findFilmById(review.getFilmId());
        Review updatedReview = reviewStorageDao.updateReview(review).get();
        eventStorageDao.updateReviewEvent(updatedReview);
        return updatedReview;
    }

    public void deleteReview(long id) {
        Review deletedReview = findReviewById(id);
        reviewStorageDao.deleteReview(id);
        eventStorageDao.deleteReviewEvent(deletedReview);
    }

    public Review findReviewById(long id) {
        reviewStorageDao.findReviewById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Отзыв с id = %s не найден", id)));
        return reviewStorageDao.findReviewById(id).get();
    }

    public List<Review> findReviewsByFilmIdOrAll(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorageDao.findAllReviews();
        }
        filmService.findFilmById(filmId);
        return reviewStorageDao.findReviewsByFilmId(filmId, count);
    }

    public void putLikeToReview(long reviewId, long userId) {
        findReviewById(reviewId);
        userService.findUserById(userId);
        if (reviewStorageDao.getLikesByReviewId(reviewId).contains(userId)) {
            throw new ProblematicLikesException("Пользователь не может поставить несколько лайков одному отзыву.");
        }
        reviewStorageDao.putLikeToReview(reviewId, userId);
    }

    public void putDislikeToReview(long reviewId, long userId) {
        findReviewById(reviewId);
        userService.findUserById(userId);
        if (reviewStorageDao.getDislikesByReviewId(reviewId).contains(userId)) {
            throw new ProblematicLikesException("Пользователь не может поставить несколько дизлайков одному отзыву.");
        }
        reviewStorageDao.putDislikeToReview(reviewId, userId);
    }

    public void deleteLikeToReview(long reviewId, long userId) {
        findReviewById(reviewId);
        userService.findUserById(userId);
        if (!reviewStorageDao.getLikesByReviewId(reviewId).contains(userId)) {
            throw new ProblematicLikesException("Пользователь не может убрать лайк, который ранее не ставил.");
        }
        reviewStorageDao.deleteLikeToReview(reviewId, userId);
    }

    public void deleteDislikeToReview(long reviewId, long userId) {
        findReviewById(reviewId);
        userService.findUserById(userId);
        if (!reviewStorageDao.getDislikesByReviewId(reviewId).contains(userId)) {
            throw new ProblematicLikesException("Пользователь не может убрать дизлайк, который ранее не ставил.");
        }
        reviewStorageDao.deleteDislikeToReview(reviewId, userId);
    }
}