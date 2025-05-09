package id.ac.ui.cs.advprog.perbaikiinaja.controller.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.coupon.CouponDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.CouponBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.services.coupon.CouponService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/coupons/admin")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * Create a coupon as an admin
     */
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coupon> createCoupon(@RequestBody CouponDto request) {
        try {

            Coupon coupon = new CouponBuilder().setDiscountValue(request.getDiscountValue())
                    .setMaxUsage(request.getMaxUsage())
                    .setExpiryDate(request.getExpiryDate())
                    .build();


            Coupon createdCoupon = couponService.createCoupon(coupon);
            return new ResponseEntity<>(createdCoupon, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Get coupons (admin only)
     */
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Coupon>> getCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return new ResponseEntity<>(coupons, HttpStatus.OK);
    }

    @PutMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
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

    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Optional<Coupon>> deleteCoupon(@PathVariable String code){
        try {
            Optional<Coupon> deleted = couponService.deleteCoupon(code);
            return ResponseEntity.ok(deleted);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


}
