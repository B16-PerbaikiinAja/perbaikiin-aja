package id.ac.ui.cs.advprog.perbaikiinaja.repository.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private UUID technician1Id;
    private UUID technician2Id;
    private UUID user1Id;
    private UUID user2Id;

    private Review review1;
    private Review review2;
    private Review review3;

    @BeforeEach
    void setUp() {
        technician1Id = UUID.randomUUID();
        technician2Id = UUID.randomUUID();
        user1Id = UUID.randomUUID();
        user2Id = UUID.randomUUID();

        review1 = Review.builder()
                .rating(5)
                .comment("Great service from technician 1 by user 1")
                .technicianId(technician1Id)
                .userId(user1Id)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();

        review2 = Review.builder()
                .rating(4)
                .comment("Good service from technician 1 by user 2")
                .technicianId(technician1Id)
                .userId(user2Id)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        review3 = Review.builder()
                .rating(3)
                .comment("Okay service from technician 2 by user 1")
                .technicianId(technician2Id)
                .userId(user1Id)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Manually invoke @PrePersist if not automatically handled by TestEntityManager in all scenarios
        // or if ID generation strategy requires it. For UUIDs generated in builder/constructor, this is fine.
        // review1.onCreate(); review2.onCreate(); review3.onCreate();


        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(review3);
        entityManager.flush();
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        entityManager.flush();
    }

    @Test
    void whenFindByTechnicianId_thenReturnReviews() {
        List<Review> foundReviews = reviewRepository.findByTechnicianId(technician1Id);

        assertThat(foundReviews).hasSize(2);
        assertThat(foundReviews).extracting(Review::getTechnicianId).containsOnly(technician1Id);
        assertThat(foundReviews).extracting(Review::getComment)
                .containsExactlyInAnyOrder(
                        "Great service from technician 1 by user 1",
                        "Good service from technician 1 by user 2"
                );
    }

    @Test
    void whenFindByTechnicianId_withNonExistentId_thenReturnEmptyList() {
        List<Review> foundReviews = reviewRepository.findByTechnicianId(UUID.randomUUID());
        assertThat(foundReviews).isEmpty();
    }

    @Test
    void whenFindByUserId_thenReturnReviews() {
        List<Review> foundReviews = reviewRepository.findByUserId(user1Id);

        assertThat(foundReviews).hasSize(2);
        assertThat(foundReviews).extracting(Review::getUserId).containsOnly(user1Id);
        assertThat(foundReviews).extracting(Review::getComment)
                .containsExactlyInAnyOrder(
                        "Great service from technician 1 by user 1",
                        "Okay service from technician 2 by user 1"
                );
    }

    @Test
    void whenFindByUserId_withNonExistentId_thenReturnEmptyList() {
        List<Review> foundReviews = reviewRepository.findByUserId(UUID.randomUUID());
        assertThat(foundReviews).isEmpty();
    }

    @Test
    void whenFindByUserIdAndTechnicianId_thenReturnReview() {
        Optional<Review> foundReview = reviewRepository.findByUserIdAndTechnicianId(user1Id, technician1Id);

        assertThat(foundReview).isPresent();
        assertThat(foundReview.get().getUserId()).isEqualTo(user1Id);
        assertThat(foundReview.get().getTechnicianId()).isEqualTo(technician1Id);
        assertThat(foundReview.get().getComment()).isEqualTo("Great service from technician 1 by user 1");
    }

    @Test
    void whenFindByUserIdAndTechnicianId_withNonExistentUser_thenReturnEmpty() {
        Optional<Review> foundReview = reviewRepository.findByUserIdAndTechnicianId(UUID.randomUUID(), technician1Id);
        assertThat(foundReview).isNotPresent();
    }

    @Test
    void whenFindByUserIdAndTechnicianId_withNonExistentTechnician_thenReturnEmpty() {
        Optional<Review> foundReview = reviewRepository.findByUserIdAndTechnicianId(user1Id, UUID.randomUUID());
        assertThat(foundReview).isNotPresent();
    }

    @Test
    void whenFindAll_thenReturnAllReviews() {
        List<Review> allReviews = reviewRepository.findAll();
        assertThat(allReviews).hasSize(3);
        assertThat(allReviews).containsExactlyInAnyOrder(review1, review2, review3);
    }

    @Test
    void whenFindById_thenReturnCorrectReview() {
        Optional<Review> foundReview = reviewRepository.findById(review1.getId());
        assertThat(foundReview).isPresent();
        assertThat(foundReview.get()).isEqualTo(review1);
    }

    @Test
    void whenSaveReview_thenItIsPersisted() {
        UUID newTechId = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();
        Review newReview = Review.builder()
                .rating(5)
                .comment("Amazing!")
                .technicianId(newTechId)
                .userId(newUserId)
                .build();
        // newReview.onCreate(); // If ID is auto-generated and not set in builder

        Review savedReview = reviewRepository.save(newReview);
        entityManager.flush(); // Ensure it's written to DB for subsequent find

        Optional<Review> foundReview = reviewRepository.findById(savedReview.getId());
        assertThat(foundReview).isPresent();
        assertThat(foundReview.get().getComment()).isEqualTo("Amazing!");
        assertThat(reviewRepository.findAll()).hasSize(4);
    }

    @Test
    void whenDeleteReview_thenItIsRemoved() {
        UUID review1Id = review1.getId();
        reviewRepository.deleteById(review1Id);
        entityManager.flush();

        Optional<Review> foundReview = reviewRepository.findById(review1Id);
        assertThat(foundReview).isNotPresent();
        assertThat(reviewRepository.findAll()).hasSize(2);
    }
}