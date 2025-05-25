package id.ac.ui.cs.advprog.perbaikiinaja.model.review;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;

import java.time.LocalDateTime;
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

    @Test
    void testOnUpdate_shouldUpdateUpdatedAt() throws InterruptedException {
        UUID technicianId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();

        Review review = Review.builder()
                .rating(4)
                .comment("Kerja Bagus!")
                .technicianId(technicianId)
                .userId(userId)
                .reportId(reportId)
                .build();

        review.onCreate();
        LocalDateTime beforeUpdate = review.getUpdatedAt();

        Thread.sleep(1000);

        review.onUpdate();
        LocalDateTime afterUpdate = review.getUpdatedAt();

        assertTrue(afterUpdate.isAfter(beforeUpdate));
    }
}