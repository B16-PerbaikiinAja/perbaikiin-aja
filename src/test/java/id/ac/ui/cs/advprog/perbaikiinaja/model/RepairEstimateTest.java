package id.ac.ui.cs.advprog.perbaikiinaja.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class RepairEstimateTest {

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
}