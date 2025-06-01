package id.ac.ui.cs.advprog.perbaikiinaja.dtos.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianSelectionDto {
    private UUID id;
    private String fullName;
    // You could add other relevant fields like averageRating if needed for selection UI
}