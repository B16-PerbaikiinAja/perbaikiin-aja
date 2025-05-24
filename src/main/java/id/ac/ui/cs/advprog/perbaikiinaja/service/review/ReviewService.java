package id.ac.ui.cs.advprog.perbaikiinaja.service.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    Review createReview(UUID userId, UUID reportId, Review review);
    Review updateReview(UUID userId, UUID reviewId, Review updatedReview);
    void deleteReview(UUID userId, UUID reviewId);
    List<Review> getReviewsForTechnician(UUID technicianId);
    double calculateAverageRating(UUID technicianId);
    List<Review> getAllReviews();
    void deleteReviewAsAdmin(UUID reviewId);
}
