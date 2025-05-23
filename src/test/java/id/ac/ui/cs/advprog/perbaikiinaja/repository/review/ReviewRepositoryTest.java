package id.ac.ui.cs.advprog.perbaikiinaja.repository.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        UUID technicianId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();
        UUID reportId1 = UUID.randomUUID();
        UUID reportId2 = UUID.randomUUID();
        UUID reportId3 = UUID.randomUUID();

        Review review1 = Review.builder()
                .userId(userId1)
                .technicianId(technicianId)
                .reportId(reportId1)
                .comment("Good service")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .userId(userId2)
                .technicianId(technicianId)
                .reportId(reportId2)
                .comment("Excellent work")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        // Review for different technician
        Review review3 = Review.builder()
                .userId(userId3)
                .technicianId(UUID.randomUUID())
                .reportId(reportId3)
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
    void whenExistsByReportIdAndUserId_thenReturnBoolean() {
        // Given
        UUID reportId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID technicianId = UUID.randomUUID();

        Review review = Review.builder()
                .userId(userId)
                .technicianId(technicianId)
                .reportId(reportId)
                .comment("Good service")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(review);
        entityManager.flush();

        // When & Then
        assertTrue(reviewRepository.existsByReportIdAndUserId(reportId, userId));
        assertFalse(reviewRepository.existsByReportIdAndUserId(reportId, UUID.randomUUID()));
        assertFalse(reviewRepository.existsByReportIdAndUserId(UUID.randomUUID(), userId));
    }

    @Test
    void whenFindByReportId_thenReturnReviews() {
        // Given
        UUID reportId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();
        UUID technicianId1 = UUID.randomUUID();
        UUID technicianId2 = UUID.randomUUID();
        UUID technicianId3 = UUID.randomUUID();

        Review review1 = Review.builder()
                .userId(userId1)
                .technicianId(technicianId1)
                .reportId(reportId)
                .comment("First review")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .userId(userId2)
                .technicianId(technicianId2)
                .reportId(reportId)
                .comment("Second review")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        // Review for different report
        Review review3 = Review.builder()
                .userId(userId3)
                .technicianId(technicianId3)
                .reportId(UUID.randomUUID())
                .comment("Different report")
                .rating(3)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(review3);
        entityManager.flush();

        // When
        List<Review> foundReviews = reviewRepository.findByReportId(reportId);

        // Then
        assertEquals(2, foundReviews.size());
        assertTrue(foundReviews.stream().allMatch(r -> r.getReportId().equals(reportId)));
    }

    @Test
    void whenFindByUserId_thenReturnReviews() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID technicianId1 = UUID.randomUUID();
        UUID technicianId2 = UUID.randomUUID();
        UUID technicianId3 = UUID.randomUUID();
        UUID reportId1 = UUID.randomUUID();
        UUID reportId2 = UUID.randomUUID();
        UUID reportId3 = UUID.randomUUID();

        Review review1 = Review.builder()
                .userId(userId)
                .technicianId(technicianId1)
                .reportId(reportId1)
                .comment("First review")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .userId(userId)
                .technicianId(technicianId2)
                .reportId(reportId2)
                .comment("Second review")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        // Review by different user
        Review review3 = Review.builder()
                .userId(UUID.randomUUID())
                .technicianId(technicianId3)
                .reportId(reportId3)
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