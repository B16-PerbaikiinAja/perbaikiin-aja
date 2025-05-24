package id.ac.ui.cs.advprog.perbaikiinaja.dtos.review;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

// This DTO might become optional if authorization is solely based on AuthenticationPrincipal
// However, keeping it for explicit operations or if admin needs to specify user for some reason.
// For user-initiated deletes, the userId in the path/principal is preferred.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteReviewRequest {
    // This field might be redundant if userId is always taken from the authenticated principal
    // For now, we assume it might be used by an admin role in some scenarios,
    // but for user deleting their own review, the principal's ID will be used.
    @NotNull(message = "User ID must be provided for deletion context if used by admin")
    private UUID userId;
}