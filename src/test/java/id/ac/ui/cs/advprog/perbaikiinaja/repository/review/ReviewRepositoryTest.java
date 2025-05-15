package id.ac.ui.cs.advprog.perbaikiinaja.repository.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.review.ReviewRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("Should save and retrieve review by technicianId")
    void testFindByTechnicianId() {
        // Arrange
        Review review = Review.builder()
                .userId(1L)
                .technicianId(1L)
                .rating(4)
                .comment("Bagus")
                .build();

        reviewRepository.save(review);

        // Act
        List<Review> reviews = reviewRepository.findByTechnicianId(1L);

        // Assert
        assertEquals(1, reviews.size());
        assertEquals(1L, reviews.get(0).getUserId());
        assertEquals(4, reviews.get(0).getRating());
        assertEquals("Bagus", reviews.get(0).getComment());
    }

    @Test
    @DisplayName("Should find review by userId")
    void testFindByUserId() {
        // Arrange
        Review review = Review.builder()
                .userId(2L)
                .technicianId(2L)
                .rating(5)
                .comment("Top markotop")
                .build();

        reviewRepository.save(review);

        // Act
        List<Review> reviews = reviewRepository.findByUserId(2L);

        // Assert
        assertEquals(1, reviews.size());
        assertEquals(2L, reviews.get(0).getUserId());
        assertEquals(5, reviews.get(0).getRating());
        assertEquals("Top markotop", reviews.get(0).getComment());
    }

    @Test
    @DisplayName("Should return null if no review found for user and technician")
    void testFindByUserId_notFound() {
        // Act
        List<Review> reviews = reviewRepository.findByUserId(999L);

        // Assert
        assertTrue(reviews.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list if no review found by technicianId")
    void testFindByTechnicianId_notFound() {
        // Act
        List<Review> reviews = reviewRepository.findByTechnicianId(999L);

        // Assert
        assertTrue(reviews.isEmpty());
    }
}
