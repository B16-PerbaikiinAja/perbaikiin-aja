package id.ac.ui.cs.advprog.perbaikiinaja.repository.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.CouponBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CouponRepositoryTest {

    @TestConfiguration
    @EntityScan(basePackages = "id.ac.ui.cs.advprog.perbaikiinaja.model.coupon")
    @EnableJpaRepositories(basePackages = "id.ac.ui.cs.advprog.perbaikiinaja.repository.coupon")
    static class Config {}

    @Autowired
    private CouponRepository couponRepository;

    private Coupon coupon1;
    private Coupon coupon2;

    @BeforeEach
    void setUp() {
        Date future1 = new Date(System.currentTimeMillis() + 86400000);
        Date future2 = new Date(System.currentTimeMillis() + 172800000);

        coupon1 = new Coupon("Coupon1", 0.15, 10, 0, future1);
        coupon2 = new Coupon("Coupon2", 0.25, 20, 0, future2);

    }

    @Test
    void testCreateCoupon() {
        Coupon savedCoupon = couponRepository.save(coupon1);
        Optional<Coupon> foundCoupon = couponRepository.findByCode(savedCoupon.getCode());
        assertTrue(foundCoupon.isPresent());
        assertEquals(coupon1.getDiscountValue(), foundCoupon.get().getDiscountValue());
        assertEquals(coupon1.getMaxUsage(), foundCoupon.get().getMaxUsage());
        assertEquals(coupon1.getExpiryDate(), foundCoupon.get().getExpiryDate());
    }

    @Test
    void testFindByCode() {
        couponRepository.save(coupon1);
        Optional<Coupon> foundCoupon = couponRepository.findByCode(coupon1.getCode());
        assertTrue(foundCoupon.isPresent());
        assertEquals(coupon1.getDiscountValue(), foundCoupon.get().getDiscountValue());
    }

    @Test
    void testFindByCodeWithNonExistentCode() {
        couponRepository.save(coupon1);
        couponRepository.save(coupon2);
        Optional<Coupon> notFoundCoupon = couponRepository.findByCode("NONEXISTING_CODE");
        assertTrue(notFoundCoupon.isEmpty());
    }

    @Test
    void testFindAll() {
        couponRepository.save(coupon1);
        couponRepository.save(coupon2);
        List<Coupon> allCoupons = couponRepository.findAll();
        assertEquals(2, allCoupons.size());
        assertTrue(allCoupons.stream().anyMatch(coupon -> coupon.getCode().equals(coupon1.getCode())));
        assertTrue(allCoupons.stream().anyMatch(coupon -> coupon.getCode().equals(coupon2.getCode())));
    }

    @Test
    void testUpdateCoupon() {
        couponRepository.save(coupon1);
        Optional<Coupon> couponToUpdateOptional = couponRepository.findByCode(coupon1.getCode());
        assertTrue(couponToUpdateOptional.isPresent());
        Coupon couponToUpdate = couponToUpdateOptional.get();

        double newDiscount = 0.40;
        int newMaxUsage = 30;
        Date newExpiryDate = new Date(System.currentTimeMillis() + 345600000);

        couponToUpdate.setDiscountValue(newDiscount);
        couponToUpdate.setMaxUsage(newMaxUsage);
        couponToUpdate.setExpiryDate(newExpiryDate);

        couponRepository.save(couponToUpdate);
        Optional<Coupon> retrievedCoupon = couponRepository.findByCode(coupon1.getCode());

        assertTrue(retrievedCoupon.isPresent());
        assertEquals(newDiscount, retrievedCoupon.get().getDiscountValue());
        assertEquals(newMaxUsage, retrievedCoupon.get().getMaxUsage());
        assertEquals(newExpiryDate, retrievedCoupon.get().getExpiryDate());
    }

    @Test
    void testDeleteCouponByCode() {
        couponRepository.save(coupon1);
        couponRepository.save(coupon2);
        couponRepository.deleteByCode(coupon1.getCode());
        Optional<Coupon> deletedCoupon = couponRepository.findByCode(coupon1.getCode());
        assertTrue(deletedCoupon.isEmpty());
        Optional<Coupon> stillExistingCoupon = couponRepository.findByCode(coupon2.getCode());
        assertTrue(stillExistingCoupon.isPresent());
    }
}