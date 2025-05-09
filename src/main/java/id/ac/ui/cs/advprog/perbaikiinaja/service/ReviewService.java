package id.ac.ui.cs.advprog.perbaikiinaja.service;

import java.util.List;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Review;

public interface ReviewService {
    Review createReview(Long userId, Long orderId, Review review);
    Review updateReview(Long userId, Long reviewId, Review updatedReview);
    void deleteReview(Long userId, Long reviewId);
    List<Review> getReviewsForTechnician(Long technicianId);
    double calculateAverageRating(Long technicianId);
    List<Review> getAllReviews();
    void deleteReviewAsAdmin(Long reviewId);
}
