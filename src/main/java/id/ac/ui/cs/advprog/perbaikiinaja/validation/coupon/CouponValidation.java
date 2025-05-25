package id.ac.ui.cs.advprog.perbaikiinaja.validation.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;

import java.security.InvalidParameterException;
import java.util.Date;

public class CouponValidation {
    public CouponValidation() {}
    public static void validateDiscountValue(double discountValue) {
        if (discountValue <= 0 || discountValue > 1) {
            throw new InvalidParameterException("Discount must be greater than 0 and at most 1");
        }
    }

    public static void validateMaxUsage(int maxUsage) {
        if (maxUsage <= 0) {
            throw new InvalidParameterException("Max usage must be greater than 0");
        }
    }

    public static void validateExpiryDate(Date expiryDate) {
        if (expiryDate == null || !expiryDate.after(new Date())) {
            throw new InvalidParameterException("Expiry date must be in the future and not null");
        }
    }

    public static void validateCouponData(Coupon coupon) {
        validateDiscountValue(coupon.getDiscountValue());
        validateMaxUsage(coupon.getMaxUsage());
        validateExpiryDate(coupon.getExpiryDate());
    }
}
