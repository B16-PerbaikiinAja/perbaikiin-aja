package id.ac.ui.cs.advprog.perbaikiinaja.controller.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.coupon.CouponDto;
import id.ac.ui.cs.advprog.perbaikiinaja.services.coupon.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;

public class CouponControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponController couponController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(couponController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateCouponSuccess() throws Exception {
        // Arrange
        Date expiryDate = new Date(System.currentTimeMillis() + 100000); // Expiry in the future
        String couponCode = "unique-code";
        Coupon validCoupon = new Coupon();
        validCoupon.setCode(couponCode);
        validCoupon.setDiscountValue(0.2);
        validCoupon.setMaxUsage(10);
        validCoupon.setExpiryDate(expiryDate);

        when(couponService.createCoupon(any(Coupon.class))).thenReturn(validCoupon);

        // Correct way to create the request JSON
        CouponDto request = new CouponDto();
        request.setCode(couponCode);
        request.setDiscountValue(0.2);
        request.setMaxUsage(10);
        request.setExpiryDate(expiryDate);
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/admin/coupons/") // Use the correct URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is(couponCode)))
                .andExpect(jsonPath("$.discountValue", is(0.2)))
                .andExpect(jsonPath("$.maxUsage", is(10)));
    }

    @Test
    public void testCreateCouponInvalidDiscountValue() throws Exception {
        // Arrange
        Date expiryDate = new Date(System.currentTimeMillis() + 100000);
        String couponCode = "unique-code";
        // Correct way to create the request JSON
        CouponDto request = new CouponDto();
        request.setCode(couponCode);
        request.setDiscountValue(1.1); // Invalid value
        request.setMaxUsage(10);
        request.setExpiryDate(expiryDate);
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/admin/coupons/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCouponInvalidMaxUsage() throws Exception {
        // Arrange
        Date expiryDate = new Date(System.currentTimeMillis() + 100000);
        String couponCode = "unique-code";
        // Correct way to create the request JSON
        CouponDto request = new CouponDto();
        request.setCode(couponCode);
        request.setDiscountValue(0.2);
        request.setMaxUsage(0); // Invalid value
        request.setExpiryDate(expiryDate);
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/admin/coupons/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCouponInvalidExpiryDate() throws Exception {
        // Arrange
        Date pastDate = new Date(System.currentTimeMillis() - 100000);
        String couponCode = "unique-code";
        // Correct way to create the request JSON
        CouponDto request = new CouponDto();
        request.setCode(couponCode);
        request.setDiscountValue(0.2);
        request.setMaxUsage(10);
        request.setExpiryDate(pastDate); // Invalid value
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/admin/coupons/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

}
