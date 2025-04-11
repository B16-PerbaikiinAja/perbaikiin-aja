package id.ac.ui.cs.advprog.perbaikiinaja.review.service;

import id.ac.ui.cs.advprog.perbaikiinaja.review.model.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(Review review);
    Review updateReview(String reviewId, Review updatedReview);
    void deleteReview(String reviewId, String userId);
    List<Review> getReviewsForTechnician(String technicianId);
}
