package id.ac.ui.cs.advprog.perbaikiinaja.review.repository;

import id.ac.ui.cs.advprog.perbaikiinaja.review.model.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
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
                .userId("user1")
                .technicianId("tech1")
                .rating(4)
                .comment("Bagus")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        List<Review> reviews = reviewRepository.findByTechnicianId("tech1");
        assertEquals(1, reviews.size());
        assertEquals("user1", reviews.get(0).getUserId());
    }

    @Test
    @DisplayName("Should find review by userId and technicianId")
    void testFindByUserIdAndTechnicianId() {
        Review review = Review.builder()
                .userId("user2")
                .technicianId("tech2")
                .rating(5)
                .comment("Top markotop")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        Review found = reviewRepository.findByUserIdAndTechnicianId("user2", "tech2");
        assertNotNull(found);
        assertEquals(5, found.getRating());
        assertEquals("Top markotop", found.getComment());
    }

    @Test
    @DisplayName("Should return null if no review found for user and technician")
    void testFindByUserIdAndTechnicianId_notFound() {
        Review found = reviewRepository.findByUserIdAndTechnicianId("ghostUser", "ghostTech");
        assertNull(found);
    }
}
