package id.ac.ui.cs.advprog.perbaikiinaja.review.service;

import java.util.List;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Review;

public interface ReviewService {
    Review createReview(Review review);
    Review updateReview(Long reviewId, Review updatedReview);
    void deleteReview(Long reviewId, Long userId);
    List<Review> getReviewsForTechnician(Long technicianId);
}