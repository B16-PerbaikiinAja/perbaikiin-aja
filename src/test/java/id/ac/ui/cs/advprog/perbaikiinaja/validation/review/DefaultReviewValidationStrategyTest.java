package id.ac.ui.cs.advprog.perbaikiinaja.validation.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
<<<<<<< HEAD

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultReviewValidationStrategyTest {

    private DefaultReviewValidationStrategy validationStrategy;
    private Review validReview;
=======
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DefaultReviewValidationStrategyTest {

    private ReviewValidationStrategy validationStrategy;
    private Review.ReviewBuilder reviewBuilder;
>>>>>>> acec46aba9db650f0e0b09ad9a94e5ec35a2c6a9

    @BeforeEach
    void setUp() {
        validationStrategy = new DefaultReviewValidationStrategy();
<<<<<<< HEAD

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
=======
        reviewBuilder = Review.builder()
                .rating(5)
                .comment("This is a valid comment that is definitely longer than ten characters.")
                .technicianId(UUID.randomUUID())
                .userId(UUID.randomUUID());
    }

    @Test
    void testValidate_validReview_noExceptionThrown() {
        Review validReview = reviewBuilder.build();
        assertDoesNotThrow(() -> validationStrategy.validate(validReview));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6, -1})
    void testValidate_invalidRating_throwsIllegalArgumentException(int invalidRating) {
        Review review = reviewBuilder.rating(invalidRating).build();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationStrategy.validate(review)
        );
        assertEquals("Rating must be between 1 and 5.", exception.getMessage());
    }

    @Test
    void testValidate_nullComment_throwsIllegalArgumentException() {
        Review review = reviewBuilder.comment(null).build();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationStrategy.validate(review)
        );
        assertEquals("Comment cannot be empty or null.", exception.getMessage());
    }

    @Test
    void testValidate_blankComment_throwsIllegalArgumentException() {
        Review review = reviewBuilder.comment("   ").build(); // Blank comment
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationStrategy.validate(review)
        );
        assertEquals("Comment cannot be empty or null.", exception.getMessage());
    }

    @Test
    void testValidate_emptyComment_throwsIllegalArgumentException() {
        Review review = reviewBuilder.comment("").build(); // Empty comment
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationStrategy.validate(review)
        );
        // Corrected Assertion: An empty string is blank, so this message is expected.
        assertEquals("Comment cannot be empty or null.", exception.getMessage());
    }


    @Test
    void testValidate_commentTooShort_throwsIllegalArgumentException() {
        Review review = reviewBuilder.comment("short").build(); // 5 chars
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationStrategy.validate(review)
        );
        assertEquals("Comment must be between 10 and 5000 characters.", exception.getMessage());
    }

    @Test
    void testValidate_commentTooLong_throwsIllegalArgumentException() {
        String longComment = "a".repeat(5001);
        Review review = reviewBuilder.comment(longComment).build();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationStrategy.validate(review)
        );
        assertEquals("Comment must be between 10 and 5000 characters.", exception.getMessage());
    }

    @Test
    void testValidate_commentMinLength_noException() {
        String minLengthComment = "1234567890"; // 10 chars
        Review review = reviewBuilder.comment(minLengthComment).build();
        assertDoesNotThrow(() -> validationStrategy.validate(review));
    }

    @Test
    void testValidate_commentMaxLength_noException() {
        String maxLengthComment = "b".repeat(5000);
        Review review = reviewBuilder.comment(maxLengthComment).build();
        assertDoesNotThrow(() -> validationStrategy.validate(review));
    }


    @Test
    void testValidate_nullTechnicianId_throwsIllegalArgumentException() {
        Review review = reviewBuilder.technicianId(null).build();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationStrategy.validate(review)
        );
        assertEquals("TechnicianId cannot be null.", exception.getMessage());
    }

    @Test
    void testValidate_nullUserId_throwsIllegalArgumentException() {
        // We manually build to ensure userId is null for this test path,
        // as the main reviewBuilder in setUp provides a default userId.
        Review review = new Review(); // Use default constructor
        review.setRating(5);
        review.setComment("Valid comment of sufficient length.");
        review.setTechnicianId(UUID.randomUUID());
        review.setUserId(null); // Explicitly set to null

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationStrategy.validate(review)
        );
        assertEquals("UserId cannot be null.", exception.getMessage());
>>>>>>> acec46aba9db650f0e0b09ad9a94e5ec35a2c6a9
    }
}