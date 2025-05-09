package id.ac.ui.cs.advprog.perbaikiinaja.service.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;

import java.util.List;
import java.util.Optional;

public interface CouponService {
    Coupon createCoupon(Coupon coupon);

    List<Coupon> getAllCoupons();

    Optional<Coupon> getCouponByCode(String code);

    Coupon useCoupon(String code);

    Optional<Coupon> updateCoupon(String code, Coupon UpdatedCouponDetails);

    Optional<Coupon> deleteCoupon(String code);
}
