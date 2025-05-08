package id.ac.ui.cs.advprog.perbaikiinaja.services.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.coupon.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon) {
        coupon = new Coupon.Builder().build();
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }

    public Optional<Coupon> updateCoupon(String code, Coupon updatedCouponDetails) {
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

    public Optional<Coupon> deleteCoupon(String code) {
        Optional<Coupon> couponToDeleteOptional = couponRepository.findByCode(code);
        couponToDeleteOptional.ifPresent(coupon -> couponRepository.deleteByCode(code));
        return couponToDeleteOptional;
    }
}