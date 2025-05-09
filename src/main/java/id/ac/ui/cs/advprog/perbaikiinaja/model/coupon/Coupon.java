package id.ac.ui.cs.advprog.perbaikiinaja.model.coupon;

import jakarta.persistence.*;
import lombok.*;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Table(name = "coupon")
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Setter
    @Id
    @Column(unique = true)
    private String code;

    @Column(nullable = false)
    private double discountValue;

    @Column(nullable = false)
    private int maxUsage;

    @Column(nullable = false)
    private int usageCount;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date expiryDate;

    public Coupon(CouponBuilder builder) {
        this.code = UUID.randomUUID().toString();
        this.discountValue = builder.discountValue;
        this.maxUsage = builder.maxUsage;
        this.expiryDate = builder.expiryDate;
        this.usageCount = 0;
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
