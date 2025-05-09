package id.ac.ui.cs.advprog.perbaikiinaja.model.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.validation.coupon.CouponValidation;
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
        CouponValidation.validateDiscountValue(discountValue);
        this.discountValue = discountValue;
    }

    public void setMaxUsage(int maxUsage) {
        CouponValidation.validateMaxUsage(maxUsage);
        this.maxUsage = maxUsage;
    }

    public void setExpiryDate(Date expiryDate) {
        CouponValidation.validateExpiryDate(expiryDate);
        this.expiryDate = expiryDate;
    }
}
