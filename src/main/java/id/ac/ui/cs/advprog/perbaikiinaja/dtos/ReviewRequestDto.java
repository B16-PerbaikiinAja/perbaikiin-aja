package id.ac.ui.cs.advprog.perbaikiinaja.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class ReviewRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long technicianId;

    @NotBlank
    private String comment;

    @Min(1)
    @Max(5)
    private int rating;
}