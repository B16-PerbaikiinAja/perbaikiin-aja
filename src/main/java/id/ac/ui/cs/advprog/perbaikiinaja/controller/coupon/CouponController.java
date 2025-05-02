package id.ac.ui.cs.advprog.perbaikiinaja.controller.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.coupon.CouponDto;
import id.ac.ui.cs.advprog.perbaikiinaja.services.coupon.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/")
    public ResponseEntity<Coupon> createCoupon(@RequestBody CouponDto request) {
        try {

            Coupon coupon = new Coupon();
            coupon.setCode(request.getCode());
            coupon.setDiscountValue(request.getDiscountValue());
            coupon.setMaxUsage(request.getMaxUsage());
            coupon.setExpiryDate(request.getExpiryDate());


            Coupon createdCoupon = couponService.createCoupon(coupon);
            return new ResponseEntity<>(createdCoupon, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return new ResponseEntity<>(coupons, HttpStatus.OK);
    }

    @PutMapping("/{code}")
    public ResponseEntity<Optional<Coupon>> updateCoupon(@PathVariable String code, @RequestBody CouponDto request) {
        try {
            Coupon updatedCoupon = new Coupon();
            updatedCoupon.setDiscountValue(request.getDiscountValue());
            updatedCoupon.setMaxUsage(request.getMaxUsage());
            updatedCoupon.setExpiryDate(request.getExpiryDate());

            Optional<Coupon> updated = couponService.updateCoupon(code, updatedCoupon);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


}
