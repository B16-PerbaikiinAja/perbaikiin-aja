package id.ac.ui.cs.advprog.perbaikiinaja.controller.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.coupon.CouponDto;
import id.ac.ui.cs.advprog.perbaikiinaja.service.coupon.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

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
        Date expiryDate = new Date(System.currentTimeMillis() + 100000);
        String couponCode = "unique-code";
        Coupon validCoupon = new Coupon();
        validCoupon.setCode(couponCode);
        validCoupon.setDiscountValue(0.2);
        validCoupon.setMaxUsage(10);
        validCoupon.setExpiryDate(expiryDate);

        when(couponService.createCoupon(any(Coupon.class))).thenReturn(validCoupon);

        CouponDto request = new CouponDto();
        request.setCode(couponCode);
        request.setDiscountValue(0.2);
        request.setMaxUsage(10);
        request.setExpiryDate(expiryDate);
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/coupons/admin/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is(couponCode)))
                .andExpect(jsonPath("$.discountValue", is(0.2)))
                .andExpect(jsonPath("$.maxUsage", is(10)));
    }

    @Test
    public void testCreateCouponInvalidDiscountValue() throws Exception {
        Date expiryDate = new Date(System.currentTimeMillis() + 100000);
        String couponCode = "unique-code";

        CouponDto request = new CouponDto();
        request.setCode(couponCode);
        request.setDiscountValue(1.1);
        request.setMaxUsage(10);
        request.setExpiryDate(expiryDate);
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/coupons/admin/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCouponInvalidMaxUsage() throws Exception {

        Date expiryDate = new Date(System.currentTimeMillis() + 100000);
        String couponCode = "unique-code";

        CouponDto request = new CouponDto();
        request.setCode(couponCode);
        request.setDiscountValue(0.2);
        request.setMaxUsage(0);
        request.setExpiryDate(expiryDate);
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/coupons/admin/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCouponInvalidExpiryDate() throws Exception {
        Date pastDate = new Date(System.currentTimeMillis() - 100000);
        String couponCode = "unique-code";

        CouponDto request = new CouponDto();
        request.setCode(couponCode);
        request.setDiscountValue(0.2);
        request.setMaxUsage(10);
        request.setExpiryDate(pastDate);
        String requestJson = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/coupons/admin/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllCouponsSuccess() throws Exception {
        List<Coupon> coupons = new ArrayList<>();
        Date expiry1 = new Date(System.currentTimeMillis() + 100000);
        Date expiry2 = new Date(System.currentTimeMillis() + 200000);
        Coupon coupon1 = new Coupon();
        coupon1.setCode("code1");
        coupon1.setDiscountValue(0.1);
        coupon1.setMaxUsage(5);
        coupon1.setExpiryDate(expiry1);

        Coupon coupon2 = new Coupon();
        coupon2.setCode("code2");
        coupon2.setDiscountValue(0.2);
        coupon2.setMaxUsage(10);
        coupon2.setExpiryDate(expiry2);

        coupons.add(coupon1);
        coupons.add(coupon2);

        when(couponService.getAllCoupons()).thenReturn(coupons);

        mockMvc.perform(get("/coupons/admin/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", is("code1")))
                .andExpect(jsonPath("$[0].discountValue", is(0.1)))
                .andExpect(jsonPath("$[0].maxUsage", is(5)))
                .andExpect(jsonPath("$[1].code", is("code2")))
                .andExpect(jsonPath("$[1].discountValue", is(0.2)))
                .andExpect(jsonPath("$[1].maxUsage", is(10)));
    }

    @Test
    public void testGetAllCouponsEmptyList() throws Exception {
        when(couponService.getAllCoupons()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/coupons/admin/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testUpdateCouponSuccess() throws Exception {
        String couponCode = "existing-code";
        Date expiryDate = new Date(System.currentTimeMillis() + 100000);
        Coupon updatedCoupon = new Coupon();
        updatedCoupon.setCode(couponCode);
        updatedCoupon.setDiscountValue(0.3);
        updatedCoupon.setMaxUsage(15);
        updatedCoupon.setExpiryDate(expiryDate);

        when(couponService.updateCoupon(eq(couponCode), any(Coupon.class))).thenReturn(Optional.of(updatedCoupon));

        CouponDto requestDto = new CouponDto();
        requestDto.setDiscountValue(0.3);
        requestDto.setMaxUsage(15);
        requestDto.setExpiryDate(expiryDate);
        String requestJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/coupons/admin/" + couponCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(couponCode)))
                .andExpect(jsonPath("$.discountValue", is(0.3)))
                .andExpect(jsonPath("$.maxUsage", is(15)));
    }

    @Test
    public void testUpdateCouponInvalidDiscountValue() throws Exception {
        String couponCode = "existing-code";
        Date expiryDate = new Date(System.currentTimeMillis() + 100000);

        CouponDto requestDto = new CouponDto();
        requestDto.setDiscountValue(1.2);
        requestDto.setMaxUsage(15);
        requestDto.setExpiryDate(expiryDate);
        String requestJson = objectMapper.writeValueAsString(requestDto);

        when(couponService.updateCoupon(eq(couponCode), any(Coupon.class)))
                .thenThrow(new IllegalArgumentException("Discount must be greater than 0 and at most 1"));

        mockMvc.perform(put("/coupons/admin/" + couponCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteCouponSuccess() throws Exception{
        String couponCode = "existing-code";
        Date expiryDate = new Date(System.currentTimeMillis() + 100000);
        Coupon deletedCoupon = new Coupon();
        deletedCoupon.setCode(couponCode);
        deletedCoupon.setDiscountValue(0.3);
        deletedCoupon.setMaxUsage(15);
        deletedCoupon.setExpiryDate(expiryDate);

        when(couponService.deleteCoupon(eq(couponCode))).thenReturn(Optional.of(deletedCoupon));

        mockMvc.perform(delete("/coupons/admin/" + couponCode)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCouponNotFound() throws Exception {
        String couponCode = "nonexistent-code";

        when(couponService.deleteCoupon(couponCode))
                .thenThrow(new IllegalArgumentException("Coupon code not found."));

        mockMvc.perform(delete("/coupons/admin/" + couponCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

