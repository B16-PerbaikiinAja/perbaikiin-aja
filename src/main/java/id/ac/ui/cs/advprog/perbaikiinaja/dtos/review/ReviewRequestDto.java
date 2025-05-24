package id.ac.ui.cs.advprog.perbaikiinaja.dtos.review;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class ReviewRequestDto {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID technicianId;

    @NotNull
    private UUID reportId;

    @NotBlank
    private String comment;

    @Min(1)
    @Max(5)
    private int rating;
}
