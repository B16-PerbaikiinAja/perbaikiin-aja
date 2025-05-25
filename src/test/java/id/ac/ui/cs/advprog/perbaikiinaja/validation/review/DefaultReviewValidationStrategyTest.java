package id.ac.ui.cs.advprog.perbaikiinaja.validation.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultReviewValidationStrategyTest {

    private DefaultReviewValidationStrategy validationStrategy;
    private Review validReview;

    @BeforeEach
    void setUp() {
        validationStrategy = new DefaultReviewValidationStrategy();

        // Set up a valid review for reuse in tests
        validReview = Review.builder()
                .id(UUID.randomUUID())
                .rating(4)
                .comment("Great service!")
                .technicianId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .reportId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void validate_WithValidReview_ShouldNotThrowException() {
        // Act & Assert - no exception should be thrown
        assertDoesNotThrow(() -> validationStrategy.validate(validReview));
    }

    @Test
    void validate_WithInvalidRating_TooLow_ShouldThrowException() {
        // Arrange
        validReview.setRating(0);  // Invalid: below min (1)

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validationStrategy.validate(validReview);
        });

        assertTrue(exception.getMessage().contains("Rating harus antara 1 dan 5"));
    }

    @Test
    void validate_WithInvalidRating_TooHigh_ShouldThrowException() {
        // Arrange
        validReview.setRating(6);  // Invalid: above max (5)

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validationStrategy.validate(validReview);
        });

        assertTrue(exception.getMessage().contains("Rating harus antara 1 dan 5"));
    }

    @ParameterizedTest
    @NullAndEmptySource  // Tests null and empty string
    @ValueSource(strings = {"   "})  // Tests blank string
    void validate_WithInvalidComment_ShouldThrowException(String comment) {
        // Arrange
        validReview.setComment(comment);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validationStrategy.validate(validReview);
        });

        assertTrue(exception.getMessage().contains("Komentar tidak boleh kosong"));
    }

    @Test
    void validate_WithNullTechnicianId_ShouldThrowException() {
        // Arrange
        validReview.setTechnicianId(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validationStrategy.validate(validReview);
        });

        assertTrue(exception.getMessage().contains("TechnicianId dan UserId tidak boleh null"));
    }

    @Test
    void validate_WithNullUserId_ShouldThrowException() {
        // Arrange
        validReview.setUserId(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validationStrategy.validate(validReview);
        });

        assertTrue(exception.getMessage().contains("TechnicianId dan UserId tidak boleh null"));
    }

    @Test
    void validate_WithBothUserIdAndTechnicianIdNull_ShouldThrowException() {
        // Arrange
        validReview.setUserId(null);
        validReview.setTechnicianId(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validationStrategy.validate(validReview);
        });

        assertTrue(exception.getMessage().contains("TechnicianId dan UserId tidak boleh null"));
    }
}