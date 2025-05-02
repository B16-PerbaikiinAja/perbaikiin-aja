package id.ac.ui.cs.advprog.perbaikiinaja.review.service;

import id.ac.ui.cs.advprog.perbaikiinaja.review.model.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(Review review);
    Review updateReview(Long reviewId, Review updatedReview);
    void deleteReview(Long reviewId, Long userId);
    List<Review> getReviewsForTechnician(Long technicianId);
}