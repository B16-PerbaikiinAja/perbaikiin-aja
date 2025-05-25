package id.ac.ui.cs.advprog.perbaikiinaja.utils;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceCalculationUtilsTest {

    // Test instances
    private Coupon mockCoupon;

    @BeforeEach
    void setUp() {
        // Set up mock coupon with 20% discount
        mockCoupon = Mockito.mock(Coupon.class);
        when(mockCoupon.getDiscountValue()).thenReturn(0.2);
    }

    // Test calculateFinalPrice method

    @Test
    void calculateFinalPrice_WithValidInputs_ReturnsCorrectPrice() {
        // Arrange
        BigDecimal originalPrice = new BigDecimal("100.00");

        // Act
        BigDecimal result = PriceCalculationUtils.calculateFinalPrice(originalPrice, mockCoupon);

        // Assert
        assertEquals(new BigDecimal("80.00"), result);
    }

    @Test
    void calculateFinalPrice_WithNullCoupon_ReturnsOriginalPrice() {
        // Arrange
        BigDecimal originalPrice = new BigDecimal("100.00");

        // Act
        BigDecimal result = PriceCalculationUtils.calculateFinalPrice(originalPrice, null);

        // Assert
        assertEquals(originalPrice, result);
    }

    @Test
    void calculateFinalPrice_WithNullOriginalPrice_ReturnsNull() {
        // Act
        BigDecimal result = PriceCalculationUtils.calculateFinalPrice(null, mockCoupon);

        // Assert
        assertNull(result);
    }

    @Test
    void calculateFinalPrice_WhenDiscountGreaterThanPrice_ReturnsZero() {
        // Arrange
        BigDecimal originalPrice = new BigDecimal("10.00");
        Coupon highDiscountCoupon = Mockito.mock(Coupon.class);
        when(highDiscountCoupon.getDiscountValue()).thenReturn(2.0); // 200% discount

        // Act
        BigDecimal result = PriceCalculationUtils.calculateFinalPrice(originalPrice, highDiscountCoupon);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }

    // Test calculateDiscountAmount method

    @Test
    void calculateDiscountAmount_WithValidInputs_ReturnsCorrectAmount() {
        // Arrange
        BigDecimal originalPrice = new BigDecimal("100.00");

        // Act
        BigDecimal result = PriceCalculationUtils.calculateDiscountAmount(originalPrice, mockCoupon);

        // Assert
        assertEquals(new BigDecimal("20.00"), result);
    }

    @Test
    void calculateDiscountAmount_WithNullCoupon_ReturnsZero() {
        // Arrange
        BigDecimal originalPrice = new BigDecimal("100.00");

        // Act
        BigDecimal result = PriceCalculationUtils.calculateDiscountAmount(originalPrice, null);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateDiscountAmount_WithNullOriginalPrice_ReturnsZero() {
        // Act
        BigDecimal result = PriceCalculationUtils.calculateDiscountAmount(null, mockCoupon);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateDiscountAmount_WithLargeDiscountAndPrice_ReturnsCorrectlyRoundedAmount() {
        // Arrange
        BigDecimal originalPrice = new BigDecimal("99.99");
        Coupon largeCoupon = Mockito.mock(Coupon.class);
        when(largeCoupon.getDiscountValue()).thenReturn(0.333); // 33.3% discount

        // Act
        BigDecimal result = PriceCalculationUtils.calculateDiscountAmount(originalPrice, largeCoupon);

        // Assert - should be 33.3% of 99.99 = 33.2967 rounded to 33.30
        assertEquals(new BigDecimal("33.30"), result);
    }

    // Test calculatePriceBreakdown method

    @Test
    void calculatePriceBreakdown_WithValidInputs_ReturnsCorrectBreakdown() {
        // Arrange
        BigDecimal originalPrice = new BigDecimal("100.00");

        // Act
        PriceCalculationUtils.PriceBreakdown breakdown =
                PriceCalculationUtils.calculatePriceBreakdown(originalPrice, mockCoupon);

        // Assert
        assertEquals(originalPrice, breakdown.getOriginalPrice());
        assertEquals(new BigDecimal("20.00"), breakdown.getDiscountAmount());
        assertEquals(new BigDecimal("80.00"), breakdown.getFinalPrice());
        assertEquals(mockCoupon, breakdown.getCoupon());
        assertEquals(20.0, breakdown.getDiscountPercentage());
    }

    @Test
    void calculatePriceBreakdown_WithNullCoupon_ReturnsBreakdownWithNoDiscount() {
        // Arrange
        BigDecimal originalPrice = new BigDecimal("100.00");

        // Act
        PriceCalculationUtils.PriceBreakdown breakdown =
                PriceCalculationUtils.calculatePriceBreakdown(originalPrice, null);

        // Assert
        assertEquals(originalPrice, breakdown.getOriginalPrice());
        assertEquals(BigDecimal.ZERO, breakdown.getDiscountAmount());
        assertEquals(originalPrice, breakdown.getFinalPrice());
        assertNull(breakdown.getCoupon());
        assertEquals(0.0, breakdown.getDiscountPercentage());
    }

    @Test
    void calculatePriceBreakdown_WithDecimalValues_ReturnsCorrectlyRoundedValues() {
        // Arrange
        BigDecimal originalPrice = new BigDecimal("123.45");
        when(mockCoupon.getDiscountValue()).thenReturn(0.15); // 15% discount

        // Act
        PriceCalculationUtils.PriceBreakdown breakdown =
                PriceCalculationUtils.calculatePriceBreakdown(originalPrice, mockCoupon);

        // Assert - discount is 123.45 * 0.15 = 18.5175, rounded to 18.52
        BigDecimal expectedDiscount = new BigDecimal("18.52");
        BigDecimal expectedFinal = originalPrice.subtract(expectedDiscount);

        assertEquals(originalPrice, breakdown.getOriginalPrice());
        assertEquals(expectedDiscount, breakdown.getDiscountAmount());
        assertEquals(expectedFinal, breakdown.getFinalPrice());
        assertEquals(15.0, breakdown.getDiscountPercentage());
    }
}