package id.ac.ui.cs.advprog.perbaikiinaja.review.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.review.dto.ReviewRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.review.dto.ReviewResponseDto;
import id.ac.ui.cs.advprog.perbaikiinaja.review.model.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.review.service.ReviewService;
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
        Review review = Review.builder()
                .userId(dto.getUserId())
                .technicianId(dto.getTechnicianId())
                .comment(dto.getComment())
                .rating(dto.getRating())
                .build();
        Review saved = reviewService.createReview(review);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto dto) {
        Review review = Review.builder()
                .userId(dto.getUserId())
                .technicianId(dto.getTechnicianId())
                .comment(dto.getComment())
                .rating(dto.getRating())
                .build();
        Review updated = reviewService.updateReview(id, review);
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
                review.getComment(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}
