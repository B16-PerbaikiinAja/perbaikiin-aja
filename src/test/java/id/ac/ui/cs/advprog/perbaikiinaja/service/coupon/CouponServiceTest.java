package id.ac.ui.cs.advprog.perbaikiinaja.service.coupon;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.CouponBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.coupon.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidParameterException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponService ;

    private Coupon coupon1;
    private Coupon coupon2;
    private final Date futureDate = new Date(System.currentTimeMillis() + 86400000);

    @BeforeEach
    void setUp() {
        Date future1 = new Date(System.currentTimeMillis() + 86400000);
        Date future2 = new Date(System.currentTimeMillis() + 172800000);

        coupon1 = new Coupon(new CouponBuilder()
                .discountValue(0.15)
                .maxUsage(10)
                .expiryDate(future1));

        coupon2 = new Coupon(new CouponBuilder()
                .discountValue(0.25)
                .maxUsage(20)
                .expiryDate(future2));
    }

    @Test
    void testCreateCoupon() {
        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);
        Coupon couponToCreate = new CouponBuilder()
                .discountValue(0.30)
                .maxUsage(5)
                .expiryDate(futureDate)
                .build();

        Coupon savedCoupon = new CouponBuilder()
                .discountValue(couponToCreate.getDiscountValue())
                .maxUsage(couponToCreate.getMaxUsage())
                .expiryDate(couponToCreate.getExpiryDate())
                .build();

        when(couponRepository.save(couponCaptor.capture())).thenReturn(savedCoupon);

        Coupon createdCoupon = couponService.createCoupon(couponToCreate);

        assertNotNull(createdCoupon);
        assertEquals(couponToCreate.getDiscountValue(), createdCoupon.getDiscountValue());
        assertEquals(couponToCreate.getMaxUsage(), createdCoupon.getMaxUsage());
        assertEquals(couponToCreate.getExpiryDate(), createdCoupon.getExpiryDate());

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void testCreateCouponWithInvalidDiscountValue() {
        Date future = new Date(System.currentTimeMillis() + 172800000);
        Coupon couponToCreate = new Coupon("valid-code", 1.5, 5, 0, future);

        assertThrows(InvalidParameterException.class, () -> couponService.createCoupon(couponToCreate));

        verify(couponRepository, never()).save(any(Coupon.class));
    }


    @Test
    void testFindAllCoupon() {
        when(couponRepository.findAll()).thenReturn(Arrays.asList(coupon1, coupon2));

        List<Coupon> retrievedCoupons = couponService.getAllCoupons();

        assertEquals(2, retrievedCoupons.size());
        assertTrue(retrievedCoupons.contains(coupon1));
        assertTrue(retrievedCoupons.contains(coupon2));
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void testFindByExistingCode() {
        when(couponRepository.findByCode(coupon1.getCode())).thenReturn(Optional.of(coupon1));

        Optional<Coupon> retrievedCoupon = couponService.getCouponByCode(coupon1.getCode());

        assertTrue(retrievedCoupon.isPresent());
        assertEquals(coupon1.getCode(), retrievedCoupon.get().getCode());
        verify(couponRepository, times(1)).findByCode(coupon1.getCode());
    }

    @Test
    void testFindNonExistingCode() {
        String nonExistingCode = UUID.randomUUID().toString();
        when(couponRepository.findByCode(nonExistingCode)).thenReturn(Optional.empty());

        Optional<Coupon> retrievedCoupon = couponService.getCouponByCode(nonExistingCode);

        assertTrue(retrievedCoupon.isEmpty());
        verify(couponRepository, times(1)).findByCode(nonExistingCode);
    }

    @Test
    void testUpdateExistingCoupon() {
        Coupon updatedDetails = new CouponBuilder()
                .discountValue(0.4)
                .maxUsage(40)
                .expiryDate(futureDate)
                .build();

        when(couponRepository.findByCode(coupon1.getCode())).thenReturn(Optional.of(coupon1));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Coupon> updatedCouponResult = couponService.updateCoupon(coupon1.getCode(), updatedDetails);

        assertTrue(updatedCouponResult.isPresent());
        assertEquals(coupon1.getCode(), updatedCouponResult.get().getCode());
        assertEquals(updatedDetails.getDiscountValue(), updatedCouponResult.get().getDiscountValue());
        assertEquals(updatedDetails.getMaxUsage(), updatedCouponResult.get().getMaxUsage());
        verify(couponRepository, times(1)).findByCode(coupon1.getCode());
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void testUpdateNonExistentCoupon() {
        String nonExistingCode = UUID.randomUUID().toString();
        Coupon updatedDetails = new CouponBuilder()
                .discountValue(0.4)
                .maxUsage(40)
                .expiryDate(futureDate)
                .build();

        when(couponRepository.findByCode(nonExistingCode)).thenReturn(Optional.empty());

        Optional<Coupon> updatedCouponResult = couponService.updateCoupon(nonExistingCode, updatedDetails);

        assertTrue(updatedCouponResult.isEmpty());
        verify(couponRepository, times(1)).findByCode(nonExistingCode);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testUpdateCouponWithInvalidDiscountValue() {
        Date future = new Date(System.currentTimeMillis() + 172800000);
        String couponCode ="valid-code";
        Coupon updatedCoupon = new Coupon(couponCode, 1.5, 5, 0, future);

        assertThrows(InvalidParameterException.class, () ->
                couponService.updateCoupon(couponCode, updatedCoupon));

        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testUpdateCouponWithInvalidMaxUsage() {
        Date future = new Date(System.currentTimeMillis() + 172800000);
        String couponCode ="valid-code";
        Coupon updatedCoupon = new Coupon(couponCode, 0.5, -1, 0, future);

        assertThrows(InvalidParameterException.class, () ->
                couponService.updateCoupon(couponCode, updatedCoupon));

        verify(couponRepository, never()).save(any(Coupon.class));
    }
    @Test
    void testUpdateCouponWithInvalidExpiryDate() {
        Date pastDate = new Date(System.currentTimeMillis() - 172800000);
        String couponCode ="valid-code";
        Coupon updatedCoupon = new Coupon(couponCode, 0.5, 5, 0, pastDate);

        assertThrows(InvalidParameterException.class, () ->
                couponService.updateCoupon(couponCode, updatedCoupon));

        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testDeleteExistingCode() {
        when(couponRepository.findByCode(coupon1.getCode())).thenReturn(Optional.of(coupon1));
        doNothing().when(couponRepository).deleteByCode(coupon1.getCode());

        couponService.deleteCoupon(coupon1.getCode());

        verify(couponRepository, times(1)).findByCode(coupon1.getCode());
        verify(couponRepository, times(1)).deleteByCode(coupon1.getCode());
    }

    @Test
    void testDeleteNonExistingCode() {
        String nonExistingCode = UUID.randomUUID().toString();
        when(couponRepository.findByCode(nonExistingCode)).thenReturn(Optional.empty());

        couponService.deleteCoupon(nonExistingCode);

        verify(couponRepository, times(1)).findByCode(nonExistingCode);
        verify(couponRepository, never()).deleteByCode(anyString());
    }

    @Test
    void testUseCouponSuccess() {
        Coupon coupon = new Coupon("Use-Coupon", 0.5, 2, 0,new Date(System.currentTimeMillis() + 86400000));

        when(couponRepository.findByCode("Use-Coupon")).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Coupon updated = couponService.useCoupon("Use-Coupon");

        assertEquals(1, updated.getUsageCount());
    }

    @Test
    void testUseCouponExpired() {
        Date pastDate = new Date(System.currentTimeMillis() - 1000);
        Coupon coupon = new Coupon("Expired-Coupon", 0.5, 2, 0, pastDate);


        when(couponRepository.findByCode("Expired-Coupon")).thenReturn(Optional.of(coupon));

        assertThrows(IllegalStateException.class, () -> couponService.useCoupon("Expired-Coupon"));
    }

    @Test
    void testUseCouponUsageLimitReached() {
        Coupon coupon = new Coupon("Used-Coupon", 0.5, 2, 2,new Date(System.currentTimeMillis() + 86400000));

        when(couponRepository.findByCode("Used-Coupon")).thenReturn(Optional.of(coupon));

        assertThrows(IllegalStateException.class, () -> couponService.useCoupon("Used-Coupon"));
    }

    @Test
    void testUseCouponNotFound() {
        when(couponRepository.findByCode("Nonexisting-Coupon")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> couponService.useCoupon("Nonexisting-Coupon"));
    }
}
