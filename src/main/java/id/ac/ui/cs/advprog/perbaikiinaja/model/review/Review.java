package id.ac.ui.cs.advprog.perbaikiinaja.model.review;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) 
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Min(1)
    @Max(5)
    @Column(nullable = false) 
    private int rating;

    @Column(nullable = false, columnDefinition = "TEXT") 
    private String comment;

    @Column(nullable = false)
    private UUID technicianId; 

    @Column(nullable = false, updatable = false) 
    private UUID userId; 

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (this.id == null) { 
            this.id = UUID.randomUUID();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}