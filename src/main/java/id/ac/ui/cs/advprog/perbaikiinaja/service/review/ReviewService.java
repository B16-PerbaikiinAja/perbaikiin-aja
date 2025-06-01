package id.ac.ui.cs.advprog.perbaikiinaja.service.review;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.TechnicianSelectionDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User; // Assuming User model is accessible

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    Review createReview(UUID userId, Review review);
    Review updateReview(UUID userId, UUID reviewId, Review updatedReview);
    void deleteReview(UUID reviewId, UUID userId);
    List<Review> getReviewsForTechnician(UUID technicianId);
    double calculateAverageRating(UUID technicianId);
    List<Review> getAllReviews();
    void deleteReviewAsAdmin(UUID reviewId); 
    List<TechnicianSelectionDto> getAvailableTechniciansForReview();
    User getUserById(UUID userId);
}