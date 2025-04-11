package id.ac.ui.cs.advprog.perbaikiinaja.review.validation;

import id.ac.ui.cs.advprog.perbaikiinaja.review.model.Review;
import org.springframework.stereotype.Component;

@Component
public class DefaultReviewValidationStrategy implements ReviewValidationStrategy {

    @Override
    public void validate(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating harus antara 1 dan 5");
        }

        if (review.getComment() == null || review.getComment().isBlank()) {
            throw new IllegalArgumentException("Komentar tidak boleh kosong");
        }

        if (review.getTechnicianId() == null || review.getUserId() == null) {
            throw new IllegalArgumentException("TechnicianId dan UserId tidak boleh null");
        }
    }
}
