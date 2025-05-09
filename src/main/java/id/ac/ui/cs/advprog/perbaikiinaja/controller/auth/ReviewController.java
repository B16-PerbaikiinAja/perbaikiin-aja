package id.ac.ui.cs.advprog.perbaikiinaja.controller.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.ReviewRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.ReviewResponseDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewRequestDto dto) {
        Long userId = dto.getUserId();
        Long orderId = dto.getOrderId();

        Review review = Review.builder()
                .userId(userId)
                .technicianId(dto.getTechnicianId())
                .orderId(orderId)
                .comment(dto.getComment())
                .rating(dto.getRating())
                .build();

        Review saved = reviewService.createReview(userId, orderId, review);

        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto dto) {
        Long userId = dto.getUserId();

        Review review = Review.builder()
                .userId(userId)
                .technicianId(dto.getTechnicianId())
                .orderId(dto.getOrderId())
                .comment(dto.getComment())
                .rating(dto.getRating())
                .build();

        Review updated = reviewService.updateReview(userId, id, review);

        return ResponseEntity.ok(toDto(updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @RequestParam Long userId) {
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/technicians/{techId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByTechnician(
            @PathVariable Long techId) {
        List<Review> reviews = reviewService.getReviewsForTechnician(techId);
        List<ReviewResponseDto> dtoList = reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    private ReviewResponseDto toDto(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getUserId(),
                review.getTechnicianId(),
                review.getOrderId(),
                review.getComment(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}
