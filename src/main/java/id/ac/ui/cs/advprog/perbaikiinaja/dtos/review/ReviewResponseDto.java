package id.ac.ui.cs.advprog.perbaikiinaja.dtos.review;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
public class ReviewResponseDto {

    private UUID id;
    private UUID userId;
    private UUID technicianId;
    private UUID reportId;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;

    public ReviewResponseDto(UUID id, UUID userId, UUID technicianId, UUID reportId, String comment, int rating, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.technicianId = technicianId;
        this.reportId = reportId;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    
}
