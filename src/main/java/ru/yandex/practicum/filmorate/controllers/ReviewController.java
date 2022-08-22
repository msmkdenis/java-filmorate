package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review findReviewById(@PathVariable long id) {
        return reviewService.findReviewById(id);
    }

    @GetMapping
    public List<Review> findReviewsByFilmIdOrAll(@RequestParam(required = false) Long filmId,
                                                 @RequestParam(defaultValue = "10") @Positive int count) {
        return reviewService.findReviewsByFilmIdOrAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewService.putLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewService.putDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteLikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteDislikeToReview(id, userId);
    }
}