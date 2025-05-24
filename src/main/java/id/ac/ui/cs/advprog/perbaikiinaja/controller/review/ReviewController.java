package id.ac.ui.cs.advprog.perbaikiinaja.controller.review;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.ReviewResponseDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.review.TechnicianSelectionDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import id.ac.ui.cs.advprog.perbaikiinaja.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews") // Base path for all review-related endpoints
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 1. Get all reviews (publicly accessible)
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getAllReviews(@AuthenticationPrincipal User currentUser) {
        List<Review> reviews = reviewService.getAllReviews();
        List<ReviewResponseDto> dtoList = reviews.stream()
                .map(review -> toDto(review, currentUser))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    // Create a new review (requires authentication)
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ReviewRequestDto dto) {
        if (currentUser == null) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated to create a review");
        }

        try {
            Review review = Review.builder()
                    // userId is set in the service from currentUser.getId()
                    .technicianId(dto.getTechnicianId())
                    .comment(dto.getComment())
                    .rating(dto.getRating())
                    .build();

            Review saved = reviewService.createReview(currentUser.getId(), review);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved, currentUser));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Update an existing review (requires authentication, user must be the author)
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ReviewRequestDto dto) {
        if (currentUser == null) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated to update a review");
        }
        try {
            Review reviewToUpdate = Review.builder()
                    .technicianId(dto.getTechnicianId()) // Keep technicianId if it's part of DTO, though usually it's fixed
                    .comment(dto.getComment())
                    .rating(dto.getRating())
                    .build();
            // The service will handle authorization by checking currentUser.getId() against the review's original userId
            Review updated = reviewService.updateReview(currentUser.getId(), id, reviewToUpdate);
            return ResponseEntity.ok(toDto(updated, currentUser));
        } catch (RuntimeException e) {
             // More specific exception handling can be added (e.g., NotFound, Forbidden)
            if (e.getMessage().toLowerCase().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else if (e.getMessage().toLowerCase().contains("authorized") || e.getMessage().toLowerCase().contains("expired")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Delete a review (requires authentication, user must be the author)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
         if (currentUser == null) {
             throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated to delete a review");
        }
        try {
            // The service will handle authorization
            reviewService.deleteReview(id, currentUser.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } else if (e.getMessage().toLowerCase().contains("authorized")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    // Admin endpoint to delete any review
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReviewAsAdmin(@PathVariable UUID id) {
        try {
            reviewService.deleteReviewAsAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    // Get all reviews for a specific technician (publicly accessible)
    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByTechnician(
            @PathVariable UUID technicianId,
            @AuthenticationPrincipal User currentUser) {
        try {
            List<Review> reviews = reviewService.getReviewsForTechnician(technicianId);
            List<ReviewResponseDto> dtoList = reviews.stream()
                    .map(review -> toDto(review, currentUser))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("not found")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // 3. Get available technicians for selection (requires authentication to see the list for writing a review)
    @GetMapping("/technicians/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TechnicianSelectionDto>> getAvailableTechnicians() {
        List<TechnicianSelectionDto> technicians = reviewService.getAvailableTechniciansForReview();
        return ResponseEntity.ok(technicians);
    }


    private ReviewResponseDto toDto(Review review, User currentUser) {
        boolean canEditDelete = false;
        if (currentUser != null && review.getUserId().equals(currentUser.getId())) {
            canEditDelete = true;
        }

        String userName = "Unknown User";
        User author = reviewService.getUserById(review.getUserId());
        if (author != null) {
            userName = author.getFullName();
        }

        String technicianName = "Unknown Technician";
        User technician = reviewService.getUserById(review.getTechnicianId());
        if (technician != null) {
            technicianName = technician.getFullName();
        }


        return new ReviewResponseDto(
                review.getId(),
                review.getUserId(),
                userName,
                review.getTechnicianId(),
                technicianName,
                review.getComment(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                canEditDelete
        );
    }
}