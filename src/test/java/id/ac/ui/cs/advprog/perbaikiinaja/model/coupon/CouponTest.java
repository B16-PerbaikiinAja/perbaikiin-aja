package id.ac.ui.cs.advprog.perbaikiinaja.model.coupon;

import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CouponTest {
    @Test
    void testBuilderCreatesCoupon() {
        Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60);

        Coupon c = new Coupon.Builder()
                .discount(0.15)
                .maxUsage(20)
                .expiryDate(future)
                .build();

        assertNotNull(c.getCode());
        assertEquals(15.0, c.getDiscountValue());
        assertEquals(20, c.getMaxUsage());
        assertEquals(future, c.getExpiryDate());
    }

    @Test
    void testDefaultConstructorExists() {
        Coupon c = new Coupon();
        assertNotNull(c.getCode());
        assertEquals(0.10, c.getDiscountValue);
        assertEquals(5, c.getMaxUsage);
        assertNotNull(c.getexpiryDate());
    }

    @Test
    void testCouponCodeIsRandom() {
        Coupon c1 = new Coupon.Builder().discountValue(0.10).maxUsage(1).build();
        Coupon c2 = new Coupon.Builder().discountValue(0.10).maxUsage(1).build();

        assertNotEquals(c1.getCode(), c2.getCode());
    }

    @Test
    void testMaxUsageCanNotBeZero() {
        assertThrows(InvalidParameterException.class, () -> {
            new Coupon.Builder().maxUsage(0).build();
        });
    }

    @Test
    void testDiscountCanNotBeZero() {
        assertThrows(InvalidParameterException.class, () -> {
            new Coupon.Builder().discountValue(0).build();
        });
    }

    @Test
    void testExpiryDateCanNotBePastDate() {
        Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60);
        assertThrows(InvalidParameterException.class, () -> {
            new Coupon.Builder().expiryDate(past).build();
        });
    }
}
