package id.ac.ui.cs.advprog.perbaikiinaja.controller.review;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.DeleteReviewRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewResponseDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewRequestDto dto) {
        UUID userId = dto.getUserId();
        UUID reportId = dto.getReportId();

        Review review = Review.builder()
                .userId(userId)
                .technicianId(dto.getTechnicianId())
                .reportId(reportId)
                .comment(dto.getComment())
                .rating(dto.getRating())
                .build();

        Review saved = reviewService.createReview(userId, reportId, review);

        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewRequestDto dto) {
        UUID userId = dto.getUserId();

        Review review = Review.builder()
                .userId(userId)
                .technicianId(dto.getTechnicianId())
                .reportId(dto.getReportId())
                .comment(dto.getComment())
                .rating(dto.getRating())
                .build();

        Review updated = reviewService.updateReview(userId, id, review);

        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID id,
            @Valid @RequestBody DeleteReviewRequest request) {  
        reviewService.deleteReview(id, request.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/technicians/{techId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByTechnician(
            @PathVariable UUID techId) {
        List<Review> reviews = reviewService.getReviewsForTechnician(techId);
        List<ReviewResponseDto> dtoList = reviews.stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    private ReviewResponseDto toDto(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getUserId(),
                review.getTechnicianId(),
                review.getReportId(),
                review.getComment(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}
