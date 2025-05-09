package id.ac.ui.cs.advprog.perbaikiinaja.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTechnicianId(Long technicianId);
    Review findByUserIdAndTechnicianId(Long userId, Long technicianId);
}