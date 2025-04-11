package id.ac.ui.cs.advprog.perbaikiinaja.review.service;

import id.ac.ui.cs.advprog.perbaikiinaja.review.model.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.review.repository.ReviewRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.review.validation.ReviewValidationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    private ReviewRepository reviewRepository;
    private ReviewValidationStrategy validationStrategy;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        validationStrategy = mock(ReviewValidationStrategy.class);
        reviewService = new ReviewServiceImpl(reviewRepository, validationStrategy);
    }

    @Test
    void testCreateReview_success() {
        Review review = Review.builder()
                .userId("user1")
                .technicianId("tech1")
                .rating(5)
                .comment("Mantap!")
                .build();

        Review savedReview = Review.builder()
                .id("rev1")
                .userId("user1")
                .technicianId("tech1")
                .rating(5)
                .comment("Mantap!")
                .build();

        when(reviewRepository.findByUserIdAndTechnicianId("user1", "tech1")).thenReturn(null);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        Review result = reviewService.createReview(review);

        assertNotNull(result);
        assertEquals("rev1", result.getId());
        assertEquals("Mantap!", result.getComment());
        assertEquals(5, result.getRating());

        verify(validationStrategy).validate(review); 
    }

    @Test
    void testCreateReview_duplicateReview_throwsException() {
        Review review = Review.builder()
                .userId("user1")
                .technicianId("tech1")
                .rating(4)
                .comment("Sudah pernah review")
                .build();

        when(reviewRepository.findByUserIdAndTechnicianId("user1", "tech1")).thenReturn(new Review());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(review);
        });

        assertEquals("Review sudah pernah dibuat", exception.getMessage());
        verify(validationStrategy).validate(review); 
    }
}
