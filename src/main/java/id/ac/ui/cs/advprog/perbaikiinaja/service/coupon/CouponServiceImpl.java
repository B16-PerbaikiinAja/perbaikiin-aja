package id.ac.ui.cs.advprog.perbaikiinaja.service.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.coupon.CouponRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.validation.coupon.CouponValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;

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