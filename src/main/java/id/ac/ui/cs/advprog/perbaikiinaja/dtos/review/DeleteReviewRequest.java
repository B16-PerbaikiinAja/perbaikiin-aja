package id.ac.ui.cs.advprog.perbaikiinaja.dtos.review;

import java.util.UUID;

public class DeleteReviewRequest {
    private UUID userId;
    
    public DeleteReviewRequest() {}
    
    public DeleteReviewRequest(UUID userId) {
        this.userId = userId;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
