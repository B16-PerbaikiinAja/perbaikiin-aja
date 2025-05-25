package id.ac.ui.cs.advprog.perbaikiinaja.service.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.review.ReviewRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.validation.review.ReviewValidationStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    private ReviewRepository reviewRepository;
    private ReviewValidationStrategy validationStrategy;
    private ReviewService reviewService;
    private UUID userId;
    private UUID reportId;
    private UUID reviewId;
    private UUID technicianId;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        validationStrategy = mock(ReviewValidationStrategy.class);
        reviewService = new ReviewServiceImpl(reviewRepository, validationStrategy);

        userId = UUID.randomUUID();
        reportId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        technicianId = UUID.randomUUID();
    }

    @Test
    void testCreateReview_success() {
        Review review = Review.builder()
                .userId(userId)
                .reportId(reportId)
                .rating(5)
                .comment("Mantap!")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewRepository.existsByReportIdAndUserId(reportId, userId)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.createReview(userId, reportId, review);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(reportId, result.getReportId());
        assertEquals("Mantap!", result.getComment());
        assertEquals(5, result.getRating());

        verify(validationStrategy).validate(review);
        verify(reviewRepository).save(review);
    }

    @Test
    void testCreateReview_duplicateReview_throwsException() {
        when(reviewRepository.existsByReportIdAndUserId(reportId, userId)).thenReturn(true);

        Review review = Review.builder()
                .userId(userId)
                .reportId(reportId)
                .rating(4)
                .comment("Sudah pernah review")
                .createdAt(LocalDateTime.now())
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(userId, reportId, review);
        });

        assertEquals("You have already reviewed this report", exception.getMessage());
        verify(validationStrategy, never()).validate(review);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testUpdateReview_success() {
        Review existingReview = Review.builder()
                .id(reviewId)
                .userId(userId)
                .reportId(reportId)
                .comment("Old Comment")
                .rating(3)
                .createdAt(LocalDateTime.now())
                .build();

        Review updatedReview = Review.builder()
                .comment("Updated Comment")
                .rating(5)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);

        Review result = reviewService.updateReview(userId, reviewId, updatedReview);

        assertEquals("Updated Comment", result.getComment());
        assertEquals(5, result.getRating());
        verify(validationStrategy).validate(existingReview);
        verify(reviewRepository).save(existingReview);
    }

    @Test
    void testDeleteReview_success() {
        Review review = Review.builder()
                .id(reviewId)
                .userId(userId)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReview(reviewId, userId);

        verify(reviewRepository).delete(review);
    }

    @Test
    void testCalculateAverageRating_success() {
        Review review1 = Review.builder().rating(5).build();
        Review review2 = Review.builder().rating(4).build();
        List<Review> reviews = List.of(review1, review2);

        when(reviewRepository.findByTechnicianId(technicianId)).thenReturn(reviews);

        double average = reviewService.calculateAverageRating(technicianId);

        assertEquals(4.5, average);
    }

    @Test
    void testGetAllReviews_success() {
        Review review = Review.builder().id(reviewId).build();
        when(reviewRepository.findAll()).thenReturn(List.of(review));

        List<Review> result = reviewService.getAllReviews();

        assertEquals(1, result.size());
        assertEquals(reviewId, result.get(0).getId());
    }

    @Test
    void testDeleteReviewAsAdmin_success() {
        Review review = Review.builder().id(reviewId).build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReviewAsAdmin(reviewId);

        verify(reviewRepository).delete(review);
    }
}