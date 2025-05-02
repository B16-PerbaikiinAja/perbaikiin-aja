package id.ac.ui.cs.advprog.perbaikiinaja.model.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class Coupon {

    @Setter
    @Id
    private String code;
    private double discountValue;
    private int maxUsage;
    private int usageCount;

    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    public Coupon(Builder builder) {
        this.code = UUID.randomUUID().toString();
        this.discountValue = builder.discountValue;
        this.maxUsage = builder.maxUsage;
        this.expiryDate = builder.expiryDate;
        this.usageCount = 0;
    }

    public Coupon(String code, double discountValue, int maxUsage, int usageCount, Date expiryDate){
        this.code = code;
        this.discountValue = discountValue;
        this.maxUsage = maxUsage;
        this.expiryDate = expiryDate;
        this.usageCount = usageCount;
    }

    public void setDiscountValue(double discountValue) {
        validateDiscountValue(discountValue);
        this.discountValue = discountValue;
    }

    public void setMaxUsage(int maxUsage) {
        validateMaxUsage(maxUsage);
        this.maxUsage = maxUsage;
    }

    public void setExpiryDate(Date expiryDate) {
        validateExpiryDate(expiryDate);
        this.expiryDate = expiryDate;
    }

    public static class Builder {

        private double discountValue;
        private int maxUsage;
        private Date expiryDate;

        public Builder() {
            this.discountValue = 0.10;
            this.maxUsage = 5;
            this.expiryDate = Date.from(
                    LocalDate.now().plusMonths(3)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
            );
        }

        public Builder setDiscountValue(double discountValue) {
            Coupon.validateDiscountValue(discountValue);
            this.discountValue = discountValue;
            return this;
        }

        public Builder setMaxUsage(int maxUsage) {
            Coupon.validateMaxUsage(maxUsage);
            this.maxUsage = maxUsage;
            return this;
        }

        public Builder setExpiryDate(Date expiryDate) {
            Coupon.validateExpiryDate(expiryDate);
            this.expiryDate = expiryDate;
            return this;
        }

        public Coupon build() {
            return new Coupon(this);
        }
    }

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
}
