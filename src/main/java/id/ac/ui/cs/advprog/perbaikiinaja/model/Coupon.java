package id.ac.ui.cs.advprog.perbaikiinaja.model;

import lombok.Getter;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.ZoneId;
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
        this(new Builder());
    }

    public Coupon(Builder builder) {
        this.code = UUID.randomUUID().toString();
        this.discountValue = builder.discountValue;
        this.maxUsage = builder.maxUsage;
        this.expiryDate = builder.expiryDate;
        this.usageCount = 0;
    }

    public static class Builder {
        private double discountValue = 0.10;
        private int maxUsage = 5;
        private Date expiryDate = Date.from(LocalDate.now()
                .plusMonths(3)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        public Builder discountValue(double discountValue) {
            if (discountValue <= 0 || discountValue > 1) {
                throw new InvalidParameterException("Discount must be greater than 0 and  at most 1");
            }

            this.discountValue = discountValue;
            return this;
        }

        public Builder maxUsage(int maxUsage) {
            if (maxUsage <= 0) {
                throw new InvalidParameterException("Max usage must be greater than 0");
            }

            this.maxUsage = maxUsage;
            return this;
        }

        public Builder expiryDate(Date expiryDate) {
            if (expiryDate == null || !expiryDate.after(new Date())) {
                throw new InvalidParameterException("Expiry date must be in the future and not null");
            }
            this.expiryDate = expiryDate;
            return this;
        }

        public Coupon build() {
            return new Coupon(this);
        }
    }
}
