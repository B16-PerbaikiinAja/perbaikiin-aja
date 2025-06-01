package id.ac.ui.cs.advprog.perbaikiinaja.validation.review;

import org.springframework.stereotype.Component;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;

@Component
public class DefaultReviewValidationStrategy implements ReviewValidationStrategy {

    @Override
    public void validate(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        if (review.getComment() == null || review.getComment().isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty or null.");
        }
         if (review.getComment().length() < 10 || review.getComment().length() > 5000) {
            throw new IllegalArgumentException("Comment must be between 10 and 5000 characters.");
        }


        if (review.getTechnicianId() == null) {
            throw new IllegalArgumentException("TechnicianId cannot be null.");
        }
        if (review.getUserId() == null) {
            throw new IllegalArgumentException("UserId cannot be null.");
        }
    }
}