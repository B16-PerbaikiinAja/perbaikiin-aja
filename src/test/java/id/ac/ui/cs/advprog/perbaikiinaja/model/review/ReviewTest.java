package id.ac.ui.cs.advprog.perbaikiinaja.model.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
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
        review = Review.builder()
                .id(reviewId)
                .rating(5)
                .comment("Excellent service!")
                .technicianId(technicianId)
                .userId(userId)
                .build();
    }

    @Test
    void testReviewCreation_setsIdFromBuilder() {
        assertEquals(reviewId, review.getId());
    }

    @Test
    void testReviewCreation_setsAllFieldsCorrectly() {
        assertEquals(5, review.getRating());
        assertEquals("Excellent service!", review.getComment());
        assertEquals(technicianId, review.getTechnicianId());
        assertEquals(userId, review.getUserId());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
        assertEquals(review.getCreatedAt(), review.getUpdatedAt());
    }

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
        assertEquals(technicianId, newReview.getTechnicianId());
        assertEquals(userId, newReview.getUserId());
    }


    @Test
    void testPrePersist_setsTimestampsAndIdIfNotSet() {
        Review newReview = new Review(); // ID will be null initially
        newReview.setRating(3);
        newReview.setComment("Okay service");
        newReview.setTechnicianId(UUID.randomUUID());
        newReview.setUserId(UUID.randomUUID());

        // Simulate PrePersist call (normally done by JPA)
        newReview.onCreate();

        assertNotNull(newReview.getId(), "ID should be set by onCreate if null");
        assertNotNull(newReview.getCreatedAt());
        assertNotNull(newReview.getUpdatedAt());
        assertEquals(newReview.getCreatedAt(), newReview.getUpdatedAt());
    }

    @Test
    void testPrePersist_doesNotOverwriteExistingId() {
        UUID preSetId = UUID.randomUUID();
        Review newReview = new Review();
        newReview.setId(preSetId); // Pre-set ID
        newReview.setRating(3);
        newReview.setComment("Okay service");
        newReview.setTechnicianId(UUID.randomUUID());
        newReview.setUserId(UUID.randomUUID());

        newReview.onCreate(); // Simulate PrePersist

        assertEquals(preSetId, newReview.getId(), "ID should not be overwritten if already set");
        assertNotNull(newReview.getCreatedAt());
        assertNotNull(newReview.getUpdatedAt());
    }


    @Test
    void testPreUpdate_updatesUpdatedAtTimestamp() throws InterruptedException {
        LocalDateTime initialCreatedAt = review.getCreatedAt();
        LocalDateTime initialUpdatedAt = review.getUpdatedAt();

        // Simulate passage of time
        Thread.sleep(10); // Sleep for a short while to ensure timestamp changes

        // Simulate PreUpdate call (normally done by JPA)
        review.onUpdate();
        LocalDateTime newUpdatedAt = review.getUpdatedAt();

        assertEquals(initialCreatedAt, review.getCreatedAt(), "CreatedAt should not change on update.");
        assertNotEquals(initialUpdatedAt, newUpdatedAt, "UpdatedAt should change on update.");
        assertTrue(newUpdatedAt.isAfter(initialUpdatedAt), "New UpdatedAt should be after initial UpdatedAt.");
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
        assertEquals(newTechnicianId, review.getTechnicianId());
    }

    @Test
    void testSetUserId() {
        UUID newUserId = UUID.randomUUID();
        review.setUserId(newUserId);
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
                .createdAt(now) // createdAt and updatedAt are usually set by @PrePersist
                .updatedAt(now)
                .build();

        assertEquals(newReviewId, builtReview.getId());
        assertEquals(1, builtReview.getRating());
        assertEquals("Poor", builtReview.getComment());
        assertEquals(newTechId, builtReview.getTechnicianId());
        assertEquals(newUsrId, builtReview.getUserId());
        assertEquals(now, builtReview.getCreatedAt());
        assertEquals(now, builtReview.getUpdatedAt());
    }
}