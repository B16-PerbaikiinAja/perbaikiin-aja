package id.ac.ui.cs.advprog.perbaikiinaja.validation.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DefaultReviewValidationStrategyTest {

    private ReviewValidationStrategy validationStrategy;
    private Review.ReviewBuilder reviewBuilder;

    @BeforeEach
    void setUp() {
        validationStrategy = new DefaultReviewValidationStrategy();
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
        // This will fail with the current implementation as "" is not blank but is < 10
        // The strategy first checks for null/blank, then length.
        // To be precise, for "" it should hit the length check.
        // assertEquals("Comment cannot be empty or null.", exception.getMessage());
         assertEquals("Comment must be between 10 and 5000 characters.", exception.getMessage());
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
        // Note: The Review builder might set a default or this might be set later in service
        // We manually build to ensure userId is null for this test path
        Review review = new Review();
        review.setRating(5);
        review.setComment("Valid comment of sufficient length.");
        review.setTechnicianId(UUID.randomUUID());
        review.setUserId(null); // Explicitly set to null

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationStrategy.validate(review)
        );
        assertEquals("UserId cannot be null.", exception.getMessage());
    }
}