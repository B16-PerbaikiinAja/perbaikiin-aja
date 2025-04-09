package id.ac.ui.cs.advprog.perbaikiinaja.model;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
public class Coupon {
    private String code;
    private double discountValue;
    private int maxUsage;
    private int usageCount;
    private Date expiryDate;

    public Coupon() {
    }

    public static class Builder {
        private double discountValue;
        private int maxUsage;
        private Date expiryDate;

        public Builder discountValue(double discountValue) {
            this.discountValue = discountValue;
            return this;
        }

        public Builder maxUsage(int maxUsage) {
            this.maxUsage = maxUsage;
            return this;
        }

        public Builder expiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Coupon build() {
            return new Coupon();
        }
    }
}
