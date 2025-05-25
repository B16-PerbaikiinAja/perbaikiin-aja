package id.ac.ui.cs.advprog.perbaikiinaja.controller.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.coupon.CouponRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.CouponBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.service.coupon.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/coupons")
public class CouponController {
    private CouponService couponService;

    /**
     * Create a coupon as an admin
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coupon> createCoupon(@RequestBody CouponRequestDto request) {
        try {

            Coupon coupon = new CouponBuilder()
                    .discountValue(request.getDiscountValue())
                    .maxUsage(request.getMaxUsage())
                    .expiryDate(request.getExpiryDate())
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
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Coupon>> getCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return new ResponseEntity<>(coupons, HttpStatus.OK);
    }

    /**
     * Get coupon detail
     */
    @GetMapping("/{code}")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable String code) {
        Optional<Coupon> coupon = couponService.getCouponByCode(code);
        return coupon
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
    }

    @PutMapping("/use/{code}")
    public ResponseEntity<Coupon> useCoupon(@PathVariable String code) {
        try {
            Coupon usedCoupon = couponService.useCoupon(code);
            return ResponseEntity.ok(usedCoupon);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update coupon detail (admin only)
     */
    @PutMapping("/admin/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable String code, @RequestBody CouponRequestDto dto) {
        try {
            Coupon coupontoUpdate = new Coupon();
            coupontoUpdate.setDiscountValue(dto.getDiscountValue());
            coupontoUpdate.setMaxUsage(dto.getMaxUsage());
            coupontoUpdate.setExpiryDate(dto.getExpiryDate());

            Optional<Coupon> updatedCoupon = couponService.updateCoupon(code, coupontoUpdate);
            return updatedCoupon
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Delete coupon (admin only)
     */
    @DeleteMapping("/admin/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCoupon(@PathVariable String code) {
        Optional<Coupon> deletedCoupon = couponService.deleteCoupon(code);
        if (deletedCoupon.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Coupon deleted successfully.");
            return ResponseEntity.ok(response);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found");
    }
}
