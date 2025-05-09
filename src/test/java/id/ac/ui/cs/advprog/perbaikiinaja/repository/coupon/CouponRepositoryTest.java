package id.ac.ui.cs.advprog.perbaikiinaja.repository.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.CouponBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CouponRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CouponRepository couponRepository;

    private Coupon coupon1;
    private Coupon coupon2;

    @BeforeEach
    void setUp() {
        Date future1 = new Date(System.currentTimeMillis() + 86400000);
        Date future2 = new Date(System.currentTimeMillis() + 172800000);

        coupon1 = new CouponBuilder()
                .discountValue(0.15)
                .maxUsage(10)
                .expiryDate(future1)
                .build();

        coupon2 = new CouponBuilder()
                .discountValue(0.25)
                .maxUsage(20)
                .expiryDate(future2)
                .build();

        entityManager.persist(coupon1);
        entityManager.persist(coupon2);
        entityManager.flush();
    }

    @Test
    void testCreateCoupon() {
        Date future = new Date(System.currentTimeMillis() + 259200000);
        Coupon newCoupon = new CouponBuilder()
                .discountValue(0.30)
                .maxUsage(5)
                .expiryDate(future)
                .build();

        Coupon savedCoupon = couponRepository.save(newCoupon);
        Optional<Coupon> foundCoupon = couponRepository.findByCode(savedCoupon.getCode());
        assertTrue(foundCoupon.isPresent());
        assertEquals(newCoupon.getDiscountValue(), foundCoupon.get().getDiscountValue());
        assertEquals(newCoupon.getMaxUsage(), foundCoupon.get().getMaxUsage());
        assertEquals(newCoupon.getExpiryDate(), foundCoupon.get().getExpiryDate());
    }

    @Test
    void testFindByCode() {
        Optional<Coupon> foundCoupon = couponRepository.findByCode(coupon1.getCode());
        assertTrue(foundCoupon.isPresent());
        assertEquals(coupon1.getDiscountValue(), foundCoupon.get().getDiscountValue());
    }

    @Test
    void testFindByCodeWithNonExistentCode() {
        Optional<Coupon> notFoundCoupon = couponRepository.findByCode("NONEXISTING_CODE");
        assertTrue(notFoundCoupon.isEmpty());
    }

    @Test
    void testFindAll() {
        List<Coupon> allCoupons = couponRepository.findAll();
        assertEquals(2, allCoupons.size());
        assertTrue(allCoupons.stream().anyMatch(coupon -> coupon.getCode().equals(coupon1.getCode())));
        assertTrue(allCoupons.stream().anyMatch(coupon -> coupon.getCode().equals(coupon2.getCode())));
    }

    @Test
    void testUpdateCoupon() {
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
        couponRepository.deleteByCode(coupon1.getCode());
        Optional<Coupon> deletedCoupon = couponRepository.findByCode(coupon1.getCode());
        assertTrue(deletedCoupon.isEmpty());
        Optional<Coupon> stillExistingCoupon = couponRepository.findByCode(coupon2.getCode());
        assertTrue(stillExistingCoupon.isPresent());
    }
}