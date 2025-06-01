package id.ac.ui.cs.advprog.perbaikiinaja.model.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
<<<<<<< HEAD

=======
import java.time.LocalDateTime;
>>>>>>> acec46aba9db650f0e0b09ad9a94e5ec35a2c6a9
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    private Review review;
    private UUID reviewId;
    private UUID technicianId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        technicianId = UUID.randomUUID();
        userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        review = Review.builder()
                .id(reviewId)
                .rating(5)
                .comment("Excellent service!")
                .technicianId(technicianId)
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void testReviewCreation_setsIdFromBuilder() {
        assertNotNull(review.getId());
        assertEquals(reviewId, review.getId());
    }

    @Test
    void testReviewCreation_setsAllFieldsCorrectly() {
        assertNotNull(review.getId());
        assertEquals(5, review.getRating());
        assertEquals("Excellent service!", review.getComment());
        assertNotNull(review.getTechnicianId());
        assertEquals(technicianId, review.getTechnicianId());
        assertNotNull(review.getUserId());
        assertEquals(userId, review.getUserId());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
        assertEquals(review.getCreatedAt(), review.getUpdatedAt());
    }
<<<<<<< HEAD
=======

    @Test
    void testReviewCreation_withNoArgsConstructor_andSetters() {
        Review newReview = new Review();
        assertNull(newReview.getId()); // ID is null before persist if not set

        newReview.setRating(4);
        newReview.setComment("Good job.");
        newReview.setTechnicianId(technicianId);
        newReview.setUserId(userId);

        assertEquals(4, newReview.getRating());
        assertEquals("Good job.", newReview.getComment());
        assertNotNull(newReview.getTechnicianId());
        assertEquals(technicianId, newReview.getTechnicianId());
        assertNotNull(newReview.getUserId());
        assertEquals(userId, newReview.getUserId());
    }

    @Test
    void testPrePersist_setsTimestampsAndIdIfNotSet() {
        Review newReview = new Review();
        assertNull(newReview.getId());
        newReview.setRating(3);
        newReview.setComment("Okay service");
        newReview.setTechnicianId(UUID.randomUUID());
        newReview.setUserId(UUID.randomUUID());

        newReview.onCreate();

        assertNotNull(newReview.getId());
        assertNotNull(newReview.getCreatedAt());
        assertNotNull(newReview.getUpdatedAt());
        assertEquals(newReview.getCreatedAt(), newReview.getUpdatedAt());
    }

    @Test
    void testPrePersist_doesNotOverwriteExistingId() {
        UUID preSetId = UUID.randomUUID();
        Review newReview = new Review();
        newReview.setId(preSetId);
        newReview.setRating(3);
        newReview.setComment("Okay service");
        newReview.setTechnicianId(UUID.randomUUID());
        newReview.setUserId(UUID.randomUUID());

        newReview.onCreate();

        assertNotNull(newReview.getId());
        assertEquals(preSetId, newReview.getId());
        assertNotNull(newReview.getCreatedAt());
        assertNotNull(newReview.getUpdatedAt());
    }

    @Test
    void testPreUpdate_updatesUpdatedAtTimestamp() {
        LocalDateTime initialCreatedAt = review.getCreatedAt();
        LocalDateTime initialUpdatedAt = review.getUpdatedAt();

        review.onUpdate();
        LocalDateTime newUpdatedAt = review.getUpdatedAt();

        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
        assertEquals(initialCreatedAt, review.getCreatedAt());
        assertTrue(newUpdatedAt.isAfter(initialUpdatedAt) || newUpdatedAt.equals(initialUpdatedAt));
    }

    @Test
    void testSetRating_validRating() {
        review.setRating(1);
        assertEquals(1, review.getRating());
        review.setRating(5);
        assertEquals(5, review.getRating());
    }

    @Test
    void testSetComment() {
        String newComment = "Updated comment.";
        review.setComment(newComment);
        assertEquals(newComment, review.getComment());
    }

    @Test
    void testSetTechnicianId() {
        UUID newTechnicianId = UUID.randomUUID();
        review.setTechnicianId(newTechnicianId);
        assertNotNull(review.getTechnicianId());
        assertEquals(newTechnicianId, review.getTechnicianId());
    }

    @Test
    void testSetUserId() {
        UUID newUserId = UUID.randomUUID();
        review.setUserId(newUserId);
        assertNotNull(review.getUserId());
        assertEquals(newUserId, review.getUserId());
    }

    @Test
    void testBuilder_allFieldsSet() {
        UUID newReviewId = UUID.randomUUID();
        UUID newTechId = UUID.randomUUID();
        UUID newUsrId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Review builtReview = Review.builder()
                .id(newReviewId)
                .rating(1)
                .comment("Poor")
                .technicianId(newTechId)
                .userId(newUsrId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertNotNull(builtReview.getId());
        assertEquals(newReviewId, builtReview.getId());
        assertEquals(1, builtReview.getRating());
        assertEquals("Poor", builtReview.getComment());
        assertNotNull(builtReview.getTechnicianId());
        assertEquals(newTechId, builtReview.getTechnicianId());
        assertNotNull(builtReview.getUserId());
        assertEquals(newUsrId, builtReview.getUserId());
        assertNotNull(builtReview.getCreatedAt());
        assertEquals(now, builtReview.getCreatedAt());
        assertNotNull(builtReview.getUpdatedAt());
        assertEquals(now, builtReview.getUpdatedAt());
    }
>>>>>>> acec46aba9db650f0e0b09ad9a94e5ec35a2c6a9
}