package id.ac.ui.cs.advprog.perbaikiinaja.repository.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void whenFindByTechnicianId_thenReturnReviews() {
        // Given
        Long technicianId = 1L;
        Review review1 = Review.builder()
                .userId(1L)
                .technicianId(technicianId)
                .orderId(1L)
                .comment("Good service")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .userId(2L)
                .technicianId(technicianId)
                .orderId(2L)
                .comment("Excellent work")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        // Review for different technician
        Review review3 = Review.builder()
                .userId(3L)
                .technicianId(2L)
                .orderId(3L)
                .comment("Average")
                .rating(3)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(review3);
        entityManager.flush();

        // When
        List<Review> foundReviews = reviewRepository.findByTechnicianId(technicianId);

        // Then
        assertEquals(2, foundReviews.size());
        assertTrue(foundReviews.stream().allMatch(r -> r.getTechnicianId().equals(technicianId)));
    }

    @Test
    void whenExistsByOrderIdAndUserId_thenReturnBoolean() {
        // Given
        Long orderId = 1L;
        Long userId = 1L;
        Review review = Review.builder()
                .userId(userId)
                .technicianId(2L)
                .orderId(orderId)
                .comment("Good service")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(review);
        entityManager.flush();

        // When & Then
        assertTrue(reviewRepository.existsByOrderIdAndUserId(orderId, userId));
        assertFalse(reviewRepository.existsByOrderIdAndUserId(orderId, 999L));
        assertFalse(reviewRepository.existsByOrderIdAndUserId(999L, userId));
    }

    @Test
    void whenFindByOrderId_thenReturnReviews() {
        // Given
        Long orderId = 1L;
        Review review1 = Review.builder()
                .userId(1L)
                .technicianId(2L)
                .orderId(orderId)
                .comment("First review")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .userId(2L)
                .technicianId(3L)
                .orderId(orderId)
                .comment("Second review")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        // Review for different order
        Review review3 = Review.builder()
                .userId(3L)
                .technicianId(4L)
                .orderId(2L)
                .comment("Different order")
                .rating(3)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(review3);
        entityManager.flush();

        // When
        List<Review> foundReviews = reviewRepository.findByOrderId(orderId);

        // Then
        assertEquals(2, foundReviews.size());
        assertTrue(foundReviews.stream().allMatch(r -> r.getOrderId().equals(orderId)));
    }

    @Test
    void whenFindByUserId_thenReturnReviews() {
        // Given
        Long userId = 1L;
        Review review1 = Review.builder()
                .userId(userId)
                .technicianId(2L)
                .orderId(1L)
                .comment("First review")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .userId(userId)
                .technicianId(3L)
                .orderId(2L)
                .comment("Second review")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        // Review by different user
        Review review3 = Review.builder()
                .userId(2L)
                .technicianId(4L)
                .orderId(3L)
                .comment("Different user")
                .rating(3)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(review3);
        entityManager.flush();

        // When
        List<Review> foundReviews = reviewRepository.findByUserId(userId);

        // Then
        assertEquals(2, foundReviews.size());
        assertTrue(foundReviews.stream().allMatch(r -> r.getUserId().equals(userId)));
    }
}