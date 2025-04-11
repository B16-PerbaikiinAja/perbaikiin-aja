package id.ac.ui.cs.advprog.perbaikiinaja.repository.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    Optional<Coupon> findByCode(String code);

    @Transactional
    @Modifying
    @Query("DELETE FROM Coupon c WHERE c.code = :code")
    void deleteByCode(String code);
}