package id.ac.ui.cs.advprog.perbaikiinaja.review.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.review.repository.ReviewRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.review.validation.ReviewValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewValidationStrategy validationStrategy;

    @Override
    public Review createReview(Review review) {
        validationStrategy.validate(review);
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long reviewId, Review updatedReview) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (!existing.getUserId().equals(updatedReview.getUserId())) {
            throw new RuntimeException("Invalid authorization to update review");
        }
        existing.setComment(updatedReview.getComment());
        existing.setRating(updatedReview.getRating());
        validationStrategy.validate(existing);
        return reviewRepository.save(existing);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
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
}
