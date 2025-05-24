package id.ac.ui.cs.advprog.perbaikiinaja.dtos.review;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor // Added for convenience
@AllArgsConstructor // Added for convenience
public class ReviewRequestDto {

    // userId is removed, will be taken from AuthenticationPrincipal

    @NotNull(message = "Technician ID cannot be null")
    private UUID technicianId;

    // reportId is removed

    @NotBlank(message = "Comment cannot be blank")
    @Size(min = 10, max = 5000, message = "Comment must be between 10 and 5000 characters")
    private String comment;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;
}