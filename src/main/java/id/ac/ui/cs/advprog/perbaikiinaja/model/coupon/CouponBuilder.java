package id.ac.ui.cs.advprog.perbaikiinaja.model.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.validation.coupon.CouponValidation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CouponBuilder {

    double discountValue;
    int maxUsage;
    Date expiryDate;

    public CouponBuilder() {
        this.discountValue = 0.10;
        this.maxUsage = 5;
        this.expiryDate = Date.from(
                LocalDate.now().plusMonths(3)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    public CouponBuilder discountValue(double discountValue) {
        CouponValidation.validateDiscountValue(discountValue);
        this.discountValue = discountValue;
        return this;
    }

    public CouponBuilder maxUsage(int maxUsage) {
        CouponValidation.validateMaxUsage(maxUsage);
        this.maxUsage = maxUsage;
        return this;
    }

    public CouponBuilder expiryDate(Date expiryDate) {
        CouponValidation.validateExpiryDate(expiryDate);
        this.expiryDate = expiryDate;
        return this;
    }

    public Coupon build() {
        return new Coupon(this);
    }
}
