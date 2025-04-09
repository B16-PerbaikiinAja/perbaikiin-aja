package id.ac.ui.cs.advprog.perbaikiinaja.model.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Coupon;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CouponTest {
    @Test
    void testBuilderCreatesCoupon() {
        Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60);

        Coupon c = new Coupon.Builder()
                .setDiscountValue(0.15)
                .setMaxUsage(20)
                .setExpiryDate(future)
                .build();

        assertNotNull(c.getCode());
        assertEquals(0.15, c.getDiscountValue());
        assertEquals(20, c.getMaxUsage());
        assertEquals(future, c.getExpiryDate());
        assertEquals(0, c.getUsageCount());
    }

    @Test
    void testDefaultValueIsCorrect() {
        Coupon c = new Coupon.Builder().build();
        assertNotNull(c.getCode());
        assertEquals(0.10, c.getDiscountValue());
        assertEquals(5, c.getMaxUsage());
        assertNotNull(c.getExpiryDate());
        assertEquals(0, c.getUsageCount());
    }

    @Test
    void testCouponCodeIsRandom() {
        Coupon c1 = new Coupon.Builder().setDiscountValue(0.10).setMaxUsage(1).build();
        Coupon c2 = new Coupon.Builder().setDiscountValue(0.10).setMaxUsage(1).build();

        assertNotEquals(c1.getCode(), c2.getCode());
    }

    @Test
    void testMaxUsageCanNotBeZero() {
        assertThrows(InvalidParameterException.class, () -> {
            new Coupon.Builder().setMaxUsage(0).build();
        });
    }

    @Test
    void testDiscountCanNotBeZero() {
        assertThrows(InvalidParameterException.class, () -> {
            new Coupon.Builder().setDiscountValue(0).build();
        });
    }

    @Test
    void testExpiryDateCanNotBePastDate() {
        Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60);
        assertThrows(InvalidParameterException.class, () -> {
            new Coupon.Builder().setExpiryDate(past).build();
        });
    }
}
