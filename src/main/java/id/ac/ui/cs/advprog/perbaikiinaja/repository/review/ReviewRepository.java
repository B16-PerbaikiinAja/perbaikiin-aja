package id.ac.ui.cs.advprog.perbaikiinaja.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository 
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByTechnicianId(UUID technicianId);

    List<Review> findByUserId(UUID userId);

    Optional<Review> findByUserIdAndTechnicianId(UUID userId, UUID technicianId);
}