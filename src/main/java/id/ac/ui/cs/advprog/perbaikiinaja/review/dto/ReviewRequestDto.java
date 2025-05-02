package id.ac.ui.cs.advprog.perbaikiinaja.review.dto;

import jakarta.validation.constraints.*;

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