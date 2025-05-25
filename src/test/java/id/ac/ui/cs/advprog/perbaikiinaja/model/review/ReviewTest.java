package id.ac.ui.cs.advprog.perbaikiinaja.model.review;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void testOnCreate_shouldSetCreatedAtAndUpdatedAt() {
        UUID technicianId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();

        Review review = Review.builder()
                .rating(5)
                .comment("Keren!")
                .technicianId(technicianId)
                .userId(userId)
                .reportId(reportId)
                .build();

        review.onCreate();

        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
        assertEquals(review.getCreatedAt(), review.getUpdatedAt());
    }
}