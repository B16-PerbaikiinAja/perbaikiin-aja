package id.ac.ui.cs.advprog.perbaikiinaja.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByTechnicianId(UUID technicianId);
    boolean existsByReportIdAndUserId(UUID reportId, UUID userId);
    List<Review> findByReportId(UUID reportId);
    List<Review> findByUserId(UUID userId);
}
