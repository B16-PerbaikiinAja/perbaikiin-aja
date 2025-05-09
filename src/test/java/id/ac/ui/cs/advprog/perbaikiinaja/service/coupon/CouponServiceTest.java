package id.ac.ui.cs.advprog.perbaikiinaja.services.coupon;

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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponService ;

    private Coupon coupon1;
    private Coupon coupon2;
    private final Date future = new Date(System.currentTimeMillis() + 86400000);

    @BeforeEach
    void setUp() {
        Date future1 = new Date(System.currentTimeMillis() + 86400000);
        Date future2 = new Date(System.currentTimeMillis() + 172800000);

        coupon1 = new Coupon(new CouponBuilder()
                .setDiscountValue(0.15)
                .setMaxUsage(10)
                .setExpiryDate(future1));

        coupon2 = new Coupon(new CouponBuilder()
                .setDiscountValue(0.25)
                .setMaxUsage(20)
                .setExpiryDate(future2));
    }

    @Test
    void testCreateCoupon() {
        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);
        Coupon couponToCreate = new CouponBuilder()
                .setDiscountValue(0.30)
                .setMaxUsage(5)
                .setExpiryDate(future)
                .build();

        Coupon savedCoupon = new CouponBuilder()
                .setDiscountValue(couponToCreate.getDiscountValue())
                .setMaxUsage(couponToCreate.getMaxUsage())
                .setExpiryDate(couponToCreate.getExpiryDate())
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
        // Create a coupon with an invalid discount value (greater than 1)
        Coupon couponToCreate = new Coupon("valid-code", 1.5, 5, 0, future);
        // We expect an InvalidParameterException to be thrown when attempting to save the coupon
        assertThrows(InvalidParameterException.class, () -> {
            couponService.createCoupon(couponToCreate);
        });

        // Ensure that save was not called on the repository since the coupon is invalid
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
                .setDiscountValue(0.4)
                .setMaxUsage(40)
                .setExpiryDate(future)
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
                .setDiscountValue(0.4)
                .setMaxUsage(40)
                .setExpiryDate(future)
                .build();

        when(couponRepository.findByCode(nonExistingCode)).thenReturn(Optional.empty());

        Optional<Coupon> updatedCouponResult = couponService.updateCoupon(nonExistingCode, updatedDetails);

        assertTrue(updatedCouponResult.isEmpty());
        verify(couponRepository, times(1)).findByCode(nonExistingCode);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testUpdateCouponWithInvalidDiscountValue() {
        Coupon updatedCoupon = new Coupon();
        updatedCoupon.setDiscountValue(1.5); // Invalid: Greater than 1
        updatedCoupon.setMaxUsage(5);
        updatedCoupon.setExpiryDate(new Date(System.currentTimeMillis() + 86400000L)); // tomorrow

        when(couponRepository.findByCode("12345")).thenReturn(Optional.of(coupon1));

        // Expecting InvalidParameterException to be thrown
        InvalidParameterException thrownException = assertThrows(InvalidParameterException.class, () -> {
            couponService.updateCoupon("12345", updatedCoupon);
        });

        // Optionally, check the exception message
        assertEquals("Discount must be greater than 0 and at most 1", thrownException.getMessage());

        verify(couponRepository, never()).save(any());  // Verify save() was not called
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
}
