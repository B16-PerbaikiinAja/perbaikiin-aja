package id.ac.ui.cs.advprog.perbaikiinaja.service.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.review.ReviewRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.service.review.ReviewService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.review.ReviewServiceImpl;
import id.ac.ui.cs.advprog.perbaikiinaja.validation.review.ReviewValidationStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
                .userId(1L)
                .orderId(1L)
                .rating(5)
                .comment("Mantap!")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewRepository.existsByOrderIdAndUserId(1L, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.createReview(1L, 1L, review);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getOrderId());
        assertEquals("Mantap!", result.getComment());
        assertEquals(5, result.getRating());

        verify(validationStrategy).validate(review);
        verify(reviewRepository).save(review);
    }

    @Test
    void testCreateReview_duplicateReview_throwsException() {
        when(reviewRepository.existsByOrderIdAndUserId(1L, 1L)).thenReturn(true);

        Review review = Review.builder()
                .userId(1L)
                .orderId(1L)
                .rating(4)
                .comment("Sudah pernah review")
                .createdAt(LocalDateTime.now())
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(1L, 1L, review);
        });

        assertEquals("You have already reviewed this order", exception.getMessage());
        verify(validationStrategy, never()).validate(review);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testUpdateReview_success() {
        Review existingReview = Review.builder()
                .id(1L)
                .userId(1L)
                .orderId(1L)
                .comment("Old Comment")
                .rating(3)
                .createdAt(LocalDateTime.now())
                .build();

        Review updatedReview = Review.builder()
                .comment("Updated Comment")
                .rating(5)
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);

        Review result = reviewService.updateReview(1L, 1L, updatedReview);

        assertEquals("Updated Comment", result.getComment());
        assertEquals(5, result.getRating());
        verify(validationStrategy).validate(existingReview);
        verify(reviewRepository).save(existingReview);
    }

    @Test
    void testDeleteReview_success() {
        Review review = Review.builder()
                .id(1L)
                .userId(1L)
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L, 1L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void testCalculateAverageRating_success() {
        Review review1 = Review.builder().rating(5).build();
        Review review2 = Review.builder().rating(4).build();
        List<Review> reviews = List.of(review1, review2);

        when(reviewRepository.findByTechnicianId(1L)).thenReturn(reviews);

        double average = reviewService.calculateAverageRating(1L);

        assertEquals(4.5, average);
    }

    @Test
    void testGetAllReviews_success() {
        Review review = Review.builder().id(1L).build();
        when(reviewRepository.findAll()).thenReturn(List.of(review));

        List<Review> result = reviewService.getAllReviews();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testDeleteReviewAsAdmin_success() {
        Review review = Review.builder().id(1L).build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReviewAsAdmin(1L);

        verify(reviewRepository).delete(review);
    }
}