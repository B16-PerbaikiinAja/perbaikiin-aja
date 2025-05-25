package id.ac.ui.cs.advprog.perbaikiinaja.service.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.coupon.CouponRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.validation.coupon.CouponValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public Coupon createCoupon(Coupon coupon) {
        CouponValidation.validateCouponData(coupon);
        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }

    /**
     * Tries to use coupon with the given code.
     * If coupon is expired, throw exception.
     * If usage exceeds maxUsage, throw exception.
     * Otherwise, increment usage count and save it
     */
    @Override
    public Coupon useCoupon(String code) {
            Coupon coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new NoSuchElementException("Coupon not found"));

            if (coupon.getExpiryDate().before(new Date())) {
                throw new IllegalStateException("Coupon is expired");
            }

            if (coupon.getUsageCount() >= coupon.getMaxUsage()) {
                throw new IllegalStateException("Coupon usage limit reached");
            }
            coupon.incrementUsageCount();
            return couponRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> updateCoupon(String code, Coupon updatedCouponDetails) {
        CouponValidation.validateCouponData(updatedCouponDetails);
        Optional<Coupon> existingCouponOptional = couponRepository.findByCode(code);
        if (existingCouponOptional.isPresent()) {
            Coupon existingCoupon = existingCouponOptional.get();
            existingCoupon.setDiscountValue(updatedCouponDetails.getDiscountValue());
            existingCoupon.setMaxUsage(updatedCouponDetails.getMaxUsage());
            existingCoupon.setExpiryDate(updatedCouponDetails.getExpiryDate());
            return Optional.of(couponRepository.save(existingCoupon));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Coupon> deleteCoupon(String code) {
        Optional<Coupon> couponToDeleteOptional = couponRepository.findByCode(code);
        couponToDeleteOptional.ifPresent(coupon -> couponRepository.deleteByCode(code));
        return couponToDeleteOptional;
    }
}