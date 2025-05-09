package id.ac.ui.cs.advprog.perbaikiinaja.model.coupon;

import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CouponTest {
    Coupon coupon1 = new CouponBuilder().build();

    @Test
    void testBuilderCreatesCoupon() {
        Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60);

        Coupon c = new CouponBuilder()
                .discountValue(0.15)
                .maxUsage(20)
                .expiryDate(future)
                .build();

        assertNotNull(c.getCode());
        assertEquals(0.15, c.getDiscountValue());
        assertEquals(20, c.getMaxUsage());
        assertEquals(future, c.getExpiryDate());
        assertEquals(0, c.getUsageCount());
    }

    @Test
    void testDefaultValueIsCorrect() {
        Coupon c = new CouponBuilder().build();
        assertNotNull(c.getCode());
        assertEquals(0.10, c.getDiscountValue());
        assertEquals(5, c.getMaxUsage());
        assertNotNull(c.getExpiryDate());
        assertEquals(0, c.getUsageCount());
    }

    @Test
    void testCouponCodeIsRandom() {
        Coupon c1 = new CouponBuilder().discountValue(0.10).maxUsage(1).build();
        Coupon c2 = new CouponBuilder().discountValue(0.10).maxUsage(1).build();

        assertNotEquals(c1.getCode(), c2.getCode());
    }

    @Test
    void testMaxUsageCanNotBeLessThanOne() {
        assertThrows(InvalidParameterException.class, () -> {
            new CouponBuilder().maxUsage(0).build();
        });

        assertThrows(InvalidParameterException.class, () -> {
            new CouponBuilder().maxUsage(-1).build();
        });
    }

    @Test
    void testDiscountValueCanNotBeZeroOrLess() {
        assertThrows(InvalidParameterException.class, () -> {
            new CouponBuilder().discountValue(0).build();
        });

        assertThrows(InvalidParameterException.class, () -> {
            new CouponBuilder().discountValue(-1).build();
        });
    }

    @Test
    void testExpiryDateCanNotBePastDate() {
        Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60);
        assertThrows(InvalidParameterException.class, () -> {
            new CouponBuilder().expiryDate(past).build();
        });
    }

    @Test
    void testUpdateValidDiscountValue() {
        coupon1.setDiscountValue(0.5);
        assertEquals(0.5, coupon1.getDiscountValue());
    }

    @Test
    void testUpdateInvalidDiscountValue() {
        assertThrows(InvalidParameterException.class, () -> {
            coupon1.setDiscountValue(0);
        });
    }

    @Test
    void testUpdateValidMaxUsage() {
        coupon1.setMaxUsage(10);
        assertEquals(10, coupon1.getMaxUsage());
    }

    @Test
    void testUpdateInvalidMaxUsage() {
        assertThrows(InvalidParameterException.class, () -> {
            coupon1.setMaxUsage(-1);
        });
    }

    @Test
    void testUpdateValidExpiryDate() {
        Date newDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
        coupon1.setExpiryDate(newDate);

        assertEquals(newDate, coupon1.getExpiryDate());
    }

    @Test
    void testUpdateInvalidExpiryDate() {
        Date invalidDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60);
        assertThrows(InvalidParameterException.class, () -> {
            coupon1.setExpiryDate(invalidDate);
        });
    }
}