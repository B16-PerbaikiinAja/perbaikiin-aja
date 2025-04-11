package id.ac.ui.cs.advprog.perbaikiinaja.services.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.coupon.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon) {
        return null;
    }

    public List<Coupon> getAllCoupons() {
        return null;
    }

    public Optional<Coupon> getCouponByCode(String code) {
        return null;
    }

    public Optional<Coupon> updateCoupon(String code, Coupon updatedCouponDetails) {
        return Optional.empty();
    }

    public void deleteCoupon(String code) {
    }

}