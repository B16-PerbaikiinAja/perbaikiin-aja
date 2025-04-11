package id.ac.ui.cs.advprog.perbaikiinaja.review.model;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.perbaikiinaja.review.model.Review;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void testOnCreate_shouldSetCreatedAtAndUpdatedAt() {
        Review review = Review.builder()
                .rating(5)
                .comment("Keren!")
                .technicianId("1")
                .userId("2")
                .build();

        review.onCreate();

        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
        assertEquals(review.getCreatedAt(), review.getUpdatedAt());
    }

    @Test
    void testOnUpdate_shouldUpdateUpdatedAt() throws InterruptedException {
        Review review = Review.builder()
                .rating(4)
                .comment("Kerja Bagus!")
                .technicianId("1")
                .userId("2")
                .build();

        review.onCreate();
        LocalDateTime beforeUpdate = review.getUpdatedAt();

        Thread.sleep(1000); 

        review.onUpdate();
        LocalDateTime afterUpdate = review.getUpdatedAt();

        assertTrue(afterUpdate.isAfter(beforeUpdate));
    }
}
