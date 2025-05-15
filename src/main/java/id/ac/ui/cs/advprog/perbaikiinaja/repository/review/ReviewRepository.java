package id.ac.ui.cs.advprog.perbaikiinaja.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.review.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTechnicianId(Long technicianId);
    boolean existsByOrderIdAndUserId(Long orderId, Long userId);
    List<Review> findByOrderId(Long orderId);
    List<Review> findByUserId(Long userId);
}
