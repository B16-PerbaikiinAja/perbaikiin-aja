package id.ac.ui.cs.advprog.perbaikiinaja.controller.review;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.DeleteReviewRequest;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private UUID userId;
    private UUID technicianId;
    private UUID reportId;
    private UUID reviewId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        technicianId = UUID.randomUUID();
        reportId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        requestDto = new ReviewRequestDto();
        requestDto.setUserId(userId);
        requestDto.setTechnicianId(technicianId);
        requestDto.setReportId(reportId);
        requestDto.setComment("Great service!");
        requestDto.setRating(5);

        review = Review.builder()
                .id(reviewId)
                .userId(userId)
                .technicianId(technicianId)
                .reportId(reportId)
                .comment("Great service!")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        responseDto = new ReviewResponseDto(
                reviewId, userId, technicianId, reportId,
                "Great service!", 5, review.getCreatedAt()
        );
    }

    @Test
    void createReview_ShouldReturnCreatedReview() {
        // Arrange
        when(reviewService.createReview(eq(requestDto.getUserId()), eq(requestDto.getReportId()), any(Review.class)))
                .thenReturn(review);

        // Act
        ResponseEntity<ReviewResponseDto> response = reviewController.createReview(requestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(review.getId(), response.getBody().getId());
        assertEquals(review.getUserId(), response.getBody().getUserId());
        assertEquals(review.getTechnicianId(), response.getBody().getTechnicianId());
        assertEquals(review.getReportId(), response.getBody().getReportId());
        assertEquals(review.getComment(), response.getBody().getComment());
        assertEquals(review.getRating(), response.getBody().getRating());

        verify(reviewService, times(1)).createReview(
                eq(requestDto.getUserId()),
                eq(requestDto.getReportId()),
                any(Review.class)
        );
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() {
        // Arrange
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
        assertEquals(review.getReportId(), response.getBody().getReportId());
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

        DeleteReviewRequest request = new DeleteReviewRequest(userId);
        doNothing().when(reviewService).deleteReview(reviewId, userId);

        // Act
        ResponseEntity<Void> response = reviewController.deleteReview(reviewId, request);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        // Verify 
        verify(reviewService, times(1)).deleteReview(reviewId, userId);
    }

    @Test
    void getReviewsByTechnician_ShouldReturnListOfReviews() {
        UUID secondUserId = UUID.randomUUID();
        UUID secondReportId = UUID.randomUUID();
        UUID secondReviewId = UUID.randomUUID();

        Review review2 = Review.builder()
                .id(secondReviewId)
                .userId(secondUserId)
                .technicianId(technicianId)
                .reportId(secondReportId)
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