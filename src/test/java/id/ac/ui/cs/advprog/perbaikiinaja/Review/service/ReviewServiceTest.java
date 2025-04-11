package id.ac.ui.cs.advprog.perbaikiinaja.review.service;

import id.ac.ui.cs.advprog.perbaikiinaja.review.model.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static java.util.Optional.of;

public class ReviewServiceTest {

    private ReviewRepository reviewRepository;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewService = new ReviewServiceImpl(reviewRepository); 
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

        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        Review result = reviewService.createReview(review);

        assertNotNull(result);
        assertEquals("rev1", result.getId());
        assertEquals("Mantap!", result.getComment());
        assertEquals(5, result.getRating());
    }
}
