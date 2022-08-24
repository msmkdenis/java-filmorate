package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        Review reviewCreate = reviewService.createReview(review);
        log.info("Добавлен отзыв {}", reviewCreate.getReviewId());
        return reviewCreate;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        Review reviewUpdate = reviewService.updateReview(review);
        log.info("Обновлен отзыв {}", reviewUpdate.getReviewId());
        return reviewUpdate;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
        log.info("Удален отзыв {}", id);
    }

    @GetMapping("/{id}")
    public Review findReviewById(@PathVariable long id) {
        Review review = reviewService.findReviewById(id);
        log.info("Получен отзыв {}", id);
        return review;
    }

    @GetMapping
    public List<Review> findReviewsByFilmIdOrAll(@RequestParam(required = false) Long filmId,
                                                 @RequestParam(defaultValue = "10") @Positive int count) {
        List<Review> reviews = reviewService.findReviewsByFilmIdOrAll(filmId, count);
        log.info("Получен список отзывов фильма {}", filmId);
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewService.putLikeToReview(id, userId);
        log.info("Пользователь {} поставил лайк отзыву {}", userId, id);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewService.putDislikeToReview(id, userId);
        log.info("Пользователь {} поставил дизлайк отзыву {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteLikeToReview(id, userId);
        log.info("Пользователь {} удалил лайк у отзыва {}", userId, id);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteDislikeToReview(id, userId);
        log.info("Пользователь {} удалил дизлайк у отзыва {}", userId, id);
    }
}