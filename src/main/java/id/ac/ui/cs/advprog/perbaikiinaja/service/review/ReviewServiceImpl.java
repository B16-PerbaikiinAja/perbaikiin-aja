package id.ac.ui.cs.advprog.perbaikiinaja.service.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.review.ReviewRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.validation.review.ReviewValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewValidationStrategy validationStrategy;
    private static final Duration REVIEW_UPDATE_WINDOW = Duration.ofDays(7);

    @Override
    public Review createReview(UUID userId, UUID reportId, Review review) {
        // Check if the review already exists for this report
        if (reviewRepository.existsByReportIdAndUserId(reportId, userId)) {
            throw new RuntimeException("You have already reviewed this report");
        }
        validationStrategy.validate(review);
        review.setUserId(userId);
        review.setReportId(reportId);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(UUID userId, UUID reviewId, Review updatedReview) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("Invalid authorization to update review");
        }

        if (Duration.between(existing.getCreatedAt(), LocalDateTime.now()).compareTo(REVIEW_UPDATE_WINDOW) > 0) {
            throw new RuntimeException("Review update window has expired");
        }

        existing.setComment(updatedReview.getComment());
        existing.setRating(updatedReview.getRating());
        validationStrategy.validate(existing);
        return reviewRepository.save(existing);
    }

    @Override
    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    @Override
    public List<Review> getReviewsForTechnician(UUID technicianId) {
        return reviewRepository.findByTechnicianId(technicianId);
    }

    @Override
    public double calculateAverageRating(UUID technicianId) {
        List<Review> reviews = reviewRepository.findByTechnicianId(technicianId);
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public void deleteReviewAsAdmin(UUID reviewId) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(existing);
    }
}
