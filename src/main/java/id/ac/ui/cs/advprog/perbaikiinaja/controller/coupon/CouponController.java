package id.ac.ui.cs.advprog.perbaikiinaja.controller.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.services.coupon.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/")
    public ResponseEntity<Coupon> createCoupon(@RequestBody CouponRequest request) {
        try {
            // Buat objek Coupon dari CouponRequest
            Coupon coupon = new Coupon(); // Use the default constructor
            coupon.setCode(request.getCode()); // set the code
            coupon.setDiscountValue(request.getDiscountValue());
            coupon.setMaxUsage(request.getMaxUsage());
            coupon.setExpiryDate(request.getExpiryDate());


            Coupon createdCoupon = couponService.createCoupon(coupon);
            return new ResponseEntity<>(createdCoupon, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Tangkap dan tangani InvalidParameterException
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Inner class untuk merepresentasikan data permintaan pembuatan kupon
    private static class CouponRequest {
        private String code;
        private double discountValue;
        private int maxUsage;
        private java.util.Date expiryDate;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public double getDiscountValue() {
            return discountValue;
        }

        public int getMaxUsage() {
            return maxUsage;
        }

        public java.util.Date getExpiryDate() {
            return expiryDate;
        }

        public void setDiscountValue(double discountValue) {
            this.discountValue = discountValue;
        }

        public void setMaxUsage(int maxUsage) {
            this.maxUsage = maxUsage;
        }

        public void setExpiryDate(java.util.Date expiryDate) {
            this.expiryDate = expiryDate;
        }
    }
}
