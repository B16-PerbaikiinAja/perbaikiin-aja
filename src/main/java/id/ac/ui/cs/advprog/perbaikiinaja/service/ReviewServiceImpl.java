package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.review.ReviewRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.validation.ReviewValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewValidationStrategy validationStrategy;
    private static final Duration REVIEW_UPDATE_WINDOW = Duration.ofDays(7);

    @Override
    public Review createReview(Long userId, Long orderId, Review review) {
        // Check if the review already exists for this order
        if (reviewRepository.existsByOrderIdAndUserId(orderId, userId)) {
            throw new RuntimeException("You have already reviewed this order");
        }
        validationStrategy.validate(review);
        review.setUserId(userId);
        review.setOrderId(orderId);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long userId, Long reviewId, Review updatedReview) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("Invalid authorization to update review");
        }
        // Check if the update is within the allowed window
        if (Duration.between(existing.getCreatedAt(), LocalDateTime.now()).compareTo(REVIEW_UPDATE_WINDOW) > 0) {
            throw new RuntimeException("Review update window has expired");
        }
        existing.setComment(updatedReview.getComment());
        existing.setRating(updatedReview.getRating());
        validationStrategy.validate(existing);
        return reviewRepository.save(existing);
    }

    @Override
    public void deleteReview(Long userId, Long reviewId) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("Invalid authorization to delete review");
        }
        reviewRepository.delete(existing);
    }

    @Override
    public List<Review> getReviewsForTechnician(Long technicianId) {
        return reviewRepository.findByTechnicianId(technicianId);
    }

    public double calculateAverageRating(Long technicianId) {
        List<Review> reviews = reviewRepository.findByTechnicianId(technicianId);
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public void deleteReviewAsAdmin(Long reviewId) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(existing);
    }
}
