package id.ac.ui.cs.advprog.perbaikiinaja.dtos.review;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor // Added for convenience
public class ReviewResponseDto {

    private UUID id;
    private UUID userId; // Author of the review
    private String userName; // Name of the user who wrote the review (optional, to be populated)
    private UUID technicianId;
    private String technicianName; // Name of the technician (optional, to be populated)
    private String comment;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Added updatedAt
    private boolean canEditDelete; // New field

    public ReviewResponseDto(UUID id, UUID userId, String userName, UUID technicianId, String technicianName, String comment, int rating, LocalDateTime createdAt, LocalDateTime updatedAt, boolean canEditDelete) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.technicianId = technicianId;
        this.technicianName = technicianName;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.canEditDelete = canEditDelete;
    }
}