package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.review.repository.ReviewRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("Should save and retrieve review by technicianId")
    void testFindByTechnicianId() {
        Review review = Review.builder()
                .userId(1L)
                .technicianId(1L)
                .rating(4)
                .comment("Bagus")
                .build();

        reviewRepository.save(review);

        List<Review> reviews = reviewRepository.findByTechnicianId(1L);
        assertEquals(1, reviews.size());
        assertEquals(1L, reviews.get(0).getUserId());
    }

    @Test
    @DisplayName("Should find review by userId and technicianId")
    void testFindByUserIdAndTechnicianId() {
        Review review = Review.builder()
                .userId(2L)
                .technicianId(2L)
                .rating(5)
                .comment("Top markotop")
                .build();

        reviewRepository.save(review);

        Review found = reviewRepository.findByUserIdAndTechnicianId(2L, 2L);
        assertNotNull(found);
        assertEquals(5, found.getRating());
        assertEquals("Top markotop", found.getComment());
    }

    @Test
    @DisplayName("Should return null if no review found for user and technician")
    void testFindByUserIdAndTechnicianId_notFound() {
        Review found = reviewRepository.findByUserIdAndTechnicianId(999L, 999L);
        assertNull(found);
    }
}