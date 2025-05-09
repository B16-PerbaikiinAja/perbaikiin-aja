package id.ac.ui.cs.advprog.perbaikiinaja.dtos;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
public class ReviewResponseDto {

    private Long id;
    private Long userId;
    private Long technicianId;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;

    public ReviewResponseDto(Long id, Long userId, Long technicianId, String comment, int rating, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.technicianId = technicianId;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
    }

}
