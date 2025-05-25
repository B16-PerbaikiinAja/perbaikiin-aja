package id.ac.ui.cs.advprog.perbaikiinaja.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.utils.PriceCalculationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

class RepairEstimateTest {

    @Mock
    private Coupon mockCoupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConstructor() {
        // Act
        RepairEstimate estimate = new RepairEstimate();

        // Assert
        assertNotNull(estimate.getId());
        assertEquals(LocalDate.now(), estimate.getCreatedDate());
        assertEquals(0.0, estimate.getCost());
        assertNull(estimate.getCompletionDate());
        assertNull(estimate.getNotes());
    }

    @Test
    void testGetId() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();

        // Act
        UUID id = estimate.getId();

        // Assert
        assertNotNull(id);
    }

    @Test
    void testGetCost() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(150.0);

        // Act
        double cost = estimate.getCost();

        // Assert
        assertEquals(150.0, cost);
    }

    @Test
    void testGetCompletionDate() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        LocalDate date = LocalDate.now().plusDays(5);
        estimate.setCompletionDate(date);

        // Act
        LocalDate completionDate = estimate.getCompletionDate();

        // Assert
        assertEquals(date, completionDate);
    }

    @Test
    void testGetAndSetNotes() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        String notes = "Test notes for repair";

        // Act
        estimate.setNotes(notes);

        // Assert
        assertEquals(notes, estimate.getNotes());
    }

    @Test
    void testGetCreatedDate() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();

        // Act
        LocalDate createdDate = estimate.getCreatedDate();

        // Assert
        assertEquals(LocalDate.now(), createdDate);
    }

    @Test
    void testValidEstimate() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));

        // Act
        boolean isValid = estimate.isValid();

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testInvalidEstimate_NegativeCost() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            estimate.setCost(-50.0);
        });

        assertTrue(exception.getMessage().contains("Cost cannot be negative"));
    }

    @Test
    void testInvalidEstimate_PastCompletionDate() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            estimate.setCompletionDate(LocalDate.now().minusDays(1));
        });

        assertTrue(exception.getMessage().contains("Completion date cannot be in the past"));
    }

    @Test
    void testSetCompletionDate_NullDate() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();

        // Act - Should not throw exception
        estimate.setCompletionDate(null);

        // Assert
        assertNull(estimate.getCompletionDate());
    }

    @Test
    void testInvalidEstimate_MissingFields() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();

        // Act
        boolean isValid = estimate.isValid();

        // Assert
        assertFalse(isValid);

        // Set only cost
        estimate.setCost(100.0);
        isValid = estimate.isValid();
        assertFalse(isValid);

        // Reset and set only completion date
        estimate = new RepairEstimate();
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        isValid = estimate.isValid();
        assertFalse(isValid);
    }

    @Test
    void testGetFinalCost_NullCoupon() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(200.0);

        // Act
        double finalCost = estimate.getFinalCost(null);

        // Assert
        assertEquals(200.0, finalCost);
    }

    @Test
    void testGetFinalCost_WithCoupon() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(200.0);

        // Use try-with-resources to ensure the mock is closed after use
        try (var mockedStatic = mockStatic(PriceCalculationUtils.class)) {
            // Mock the PriceCalculationUtils behavior
            mockedStatic.when(() -> PriceCalculationUtils.calculateFinalPrice(
                            BigDecimal.valueOf(200.0), mockCoupon))
                    .thenReturn(BigDecimal.valueOf(160.0));

            // Act
            double finalCost = estimate.getFinalCost(mockCoupon);

            // Assert
            assertEquals(160.0, finalCost);
        }
    }

    @Test
    void testGetDiscountAmount_NullCoupon() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(200.0);

        // Act
        double discountAmount = estimate.getDiscountAmount(null);

        // Assert
        assertEquals(0.0, discountAmount);
    }

    @Test
    void testGetDiscountAmount_WithCoupon() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(200.0);

        // Use try-with-resources to ensure the mock is closed after use
        try (var mockedStatic = mockStatic(PriceCalculationUtils.class)) {
            // Mock the PriceCalculationUtils behavior
            mockedStatic.when(() -> PriceCalculationUtils.calculateDiscountAmount(
                            BigDecimal.valueOf(200.0), mockCoupon))
                    .thenReturn(BigDecimal.valueOf(40.0));

            // Act
            double discountAmount = estimate.getDiscountAmount(mockCoupon);

            // Assert
            assertEquals(40.0, discountAmount);
        }
    }
}