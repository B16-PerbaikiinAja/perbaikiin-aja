package id.ac.ui.cs.advprog.perbaikiinaja.controller.review;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewResponseDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.service.review.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewRequestDto requestDto;
    private Review review;
    private ReviewResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new ReviewRequestDto();
        requestDto.setUserId(1L);
        requestDto.setTechnicianId(2L);
        requestDto.setOrderId(3L);
        requestDto.setComment("Great service!");
        requestDto.setRating(5);

        review = Review.builder()
                .id(1L)
                .userId(1L)
                .technicianId(2L)
                .orderId(3L)
                .comment("Great service!")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        responseDto = new ReviewResponseDto(
                1L, 1L, 2L, 3L, "Great service!", 5, review.getCreatedAt()
        );
    }

    @Test
    void createReview_ShouldReturnCreatedReview() {
        // Arrange
        when(reviewService.createReview(eq(requestDto.getUserId()), eq(requestDto.getOrderId()), any(Review.class)))
                .thenReturn(review);

        // Act
        ResponseEntity<ReviewResponseDto> response = reviewController.createReview(requestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(review.getId(), response.getBody().getId());
        assertEquals(review.getUserId(), response.getBody().getUserId());
        assertEquals(review.getTechnicianId(), response.getBody().getTechnicianId());
        assertEquals(review.getOrderId(), response.getBody().getOrderId());
        assertEquals(review.getComment(), response.getBody().getComment());
        assertEquals(review.getRating(), response.getBody().getRating());

        verify(reviewService, times(1)).createReview(
                eq(requestDto.getUserId()),
                eq(requestDto.getOrderId()),
                any(Review.class)
        );
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() {
        // Arrange
        Long reviewId = 1L;
        when(reviewService.updateReview(
                eq(requestDto.getUserId()),
                eq(reviewId),
                any(Review.class))
        ).thenReturn(review);

        // Act
        ResponseEntity<ReviewResponseDto> response = reviewController.updateReview(reviewId, requestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(review.getId(), response.getBody().getId());
        assertEquals(review.getUserId(), response.getBody().getUserId());
        assertEquals(review.getTechnicianId(), response.getBody().getTechnicianId());
        assertEquals(review.getOrderId(), response.getBody().getOrderId());
        assertEquals(review.getComment(), response.getBody().getComment());
        assertEquals(review.getRating(), response.getBody().getRating());

        verify(reviewService, times(1)).updateReview(
                eq(requestDto.getUserId()),
                eq(reviewId),
                any(Review.class)
        );
    }

    @Test
    void deleteReview_ShouldReturnNoContent() {
        Long reviewId = 1L;
        Long userId = 1L;

        ResponseEntity<Void> response = reviewController.deleteReview(reviewId, userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(reviewService, times(1)).deleteReview(reviewId, userId);
    }

    @Test
    void getReviewsByTechnician_ShouldReturnListOfReviews() {
        Long technicianId = 2L;
        Review review2 = Review.builder()
                .id(2L)
                .userId(3L)
                .technicianId(2L)
                .orderId(4L)
                .comment("Good job")
                .rating(4)
                .createdAt(LocalDateTime.now())
                .build();

        List<Review> reviews = Arrays.asList(review, review2);
        when(reviewService.getReviewsForTechnician(technicianId)).thenReturn(reviews);

        ResponseEntity<List<ReviewResponseDto>> response = reviewController.getReviewsByTechnician(technicianId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(technicianId, response.getBody().get(0).getTechnicianId());
        assertEquals(technicianId, response.getBody().get(1).getTechnicianId());

        verify(reviewService, times(1)).getReviewsForTechnician(technicianId);
    }

}