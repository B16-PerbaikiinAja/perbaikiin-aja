package id.ac.ui.cs.advprog.perbaikiinaja.service.review;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.TechnicianSelectionDto;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.review.ReviewRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.validation.review.ReviewValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import for @Transactional

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository; // Inject UserRepository
    private final ReviewValidationStrategy validationStrategy;
    private static final Duration REVIEW_UPDATE_WINDOW = Duration.ofDays(7); // Configurable

    @Override
    @Transactional
    public Review createReview(UUID userId, Review review) {
        // Ensure the user creating the review exists (optional, Spring Security might handle this)
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure the technician being reviewed exists
        userRepository.findById(review.getTechnicianId())
                .filter(user -> user.getRole().equals(UserRole.TECHNICIAN.getValue()))
                .orElseThrow(() -> new RuntimeException("Technician not found or user is not a technician"));

        // Optional: Check if this user has already reviewed this technician
        Optional<Review> existingReview = reviewRepository.findByUserIdAndTechnicianId(userId, review.getTechnicianId());
        if (existingReview.isPresent()) {
            throw new RuntimeException("You have already reviewed this technician.");
        }

        review.setUserId(userId); // Set the author of the review
        // reportId is removed

        validationStrategy.validate(review); // Validate before setting timestamps
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now()); // Ensure updatedAt is set on creation
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review updateReview(UUID userId, UUID reviewId, Review updatedReview) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));

        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this review.");
        }

        if (Duration.between(existing.getCreatedAt(), LocalDateTime.now()).compareTo(REVIEW_UPDATE_WINDOW) > 0) {
            throw new RuntimeException("Review update window of " + REVIEW_UPDATE_WINDOW.toDays() + " days has expired.");
        }

        // Ensure technicianId is not changed during update, or handle if it's allowed
        if (!existing.getTechnicianId().equals(updatedReview.getTechnicianId())) {
            // This might indicate an attempt to change the review to be for a different technician,
            // which is usually not allowed. Or, technicianId might not be part of updatedReview DTO for updates.
            // For now, we assume technicianId is part of the DTO for update, but it's better if it's not,
            // and only comment/rating are updatable.
            // If technicianId is not in ReviewRequestDto for update, then this check is not needed for updatedReview.getTechnicianId().
             throw new IllegalArgumentException("Cannot change the technician for an existing review.");
        }


        existing.setComment(updatedReview.getComment());
        existing.setRating(updatedReview.getRating());
        // userId and technicianId should remain the same from the existing review

        validationStrategy.validate(existing); // Validate the updated state
        // PreUpdate in Review entity will handle updatedAt
        return reviewRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this review.");
        }

        reviewRepository.delete(review);
    }

    @Override
    public List<Review> getReviewsForTechnician(UUID technicianId) {
        // Ensure the technicianId corresponds to a valid technician
        userRepository.findById(technicianId)
            .filter(user -> user.getRole().equals(UserRole.TECHNICIAN.getValue()))
            .orElseThrow(() -> new RuntimeException("Technician not found or user is not a technician with ID: " + technicianId));
        return reviewRepository.findByTechnicianId(technicianId);
    }

    @Override
    public double calculateAverageRating(UUID technicianId) {
        List<Review> reviews = getReviewsForTechnician(technicianId); // Use the validated method
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteReviewAsAdmin(UUID reviewId) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));
        reviewRepository.delete(existing);
    }

    @Override
    public List<TechnicianSelectionDto> getAvailableTechniciansForReview() {
        return userRepository.findByRole(UserRole.TECHNICIAN.getValue()).stream()
                .map(user -> new TechnicianSelectionDto(user.getId(), user.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElse(null); // Return null or throw exception based on desired behavior
    }
}