package id.ac.ui.cs.advprog.perbaikiinaja.utils;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceCalculationUtils {

    private PriceCalculationUtils(){}

    /**
     * Calculate final price after applying coupon discount
     * @param originalPrice The original price before discount
     * @param coupon The coupon to apply (can be null)
     * @return The final price after discount
     */
    public static BigDecimal calculateFinalPrice(BigDecimal originalPrice, Coupon coupon) {
        if (coupon == null || originalPrice == null) {
            return originalPrice;
        }

        BigDecimal discountAmount = calculateDiscountAmount(originalPrice, coupon);
        BigDecimal finalPrice = originalPrice.subtract(discountAmount);

        // Ensure final price is not negative
        return finalPrice.max(BigDecimal.ZERO);
    }

    /**
     * Calculate discount amount from coupon
     * @param originalPrice The original price
     * @param coupon The coupon to apply
     * @return The discount amount
     */
    public static BigDecimal calculateDiscountAmount(BigDecimal originalPrice, Coupon coupon) {
        if (coupon == null || originalPrice == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountPercentage = BigDecimal.valueOf(coupon.getDiscountValue());
        return originalPrice.multiply(discountPercentage).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate price breakdown with coupon
     * @param originalPrice The original price
     * @param coupon The coupon to apply
     * @return PriceBreakdown object with details
     */
    public static PriceBreakdown calculatePriceBreakdown(BigDecimal originalPrice, Coupon coupon) {
        BigDecimal discountAmount = calculateDiscountAmount(originalPrice, coupon);
        BigDecimal finalPrice = calculateFinalPrice(originalPrice, coupon);

        return new PriceBreakdown(originalPrice, discountAmount, finalPrice, coupon);
    }

    /**
     * Inner class to hold price breakdown details
     */
    public static class PriceBreakdown {
        private final BigDecimal originalPrice;
        private final BigDecimal discountAmount;
        private final BigDecimal finalPrice;
        private final Coupon coupon;

        public PriceBreakdown(BigDecimal originalPrice, BigDecimal discountAmount, BigDecimal finalPrice, Coupon coupon) {
            this.originalPrice = originalPrice;
            this.discountAmount = discountAmount;
            this.finalPrice = finalPrice;
            this.coupon = coupon;
        }

        // Getters
        public BigDecimal getOriginalPrice() { return originalPrice; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public BigDecimal getFinalPrice() { return finalPrice; }
        public Coupon getCoupon() { return coupon; }
        public double getDiscountPercentage() {
            return coupon != null ? coupon.getDiscountValue() * 100 : 0;
        }
    }
}