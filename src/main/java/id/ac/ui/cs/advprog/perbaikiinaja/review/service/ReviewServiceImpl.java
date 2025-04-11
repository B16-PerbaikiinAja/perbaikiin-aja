package id.ac.ui.cs.advprog.perbaikiinaja.review.service;

import id.ac.ui.cs.advprog.perbaikiinaja.review.model.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.review.repository.ReviewRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.review.validation.ReviewValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewValidationStrategy validationStrategy;

    @Override
    public Review createReview(Review review) {
        validationStrategy.validate(review);

        Review existing = reviewRepository.findByUserIdAndTechnicianId(review.getUserId(), review.getTechnicianId());
        if (existing != null) {
            throw new RuntimeException("Review already exists");
        }

        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(String reviewId, Review updatedReview) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(updatedReview.getUserId())) {
            throw new RuntimeException("Invalid authorization to update review");
        }

        review.setRating(updatedReview.getRating());
        review.setComment(updatedReview.getComment());
        review.setUpdatedAt(LocalDateTime.now());

        validationStrategy.validate(review);

        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(String reviewId, String userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Invalid authorization to delete review");
        }

        reviewRepository.delete(review);
    }

    @Override
    public List<Review> getReviewsForTechnician(String technicianId) {
        return reviewRepository.findByTechnicianId(technicianId);
    }
}
