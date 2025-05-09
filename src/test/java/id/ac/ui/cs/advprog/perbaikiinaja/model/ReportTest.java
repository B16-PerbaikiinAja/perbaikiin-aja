package id.ac.ui.cs.advprog.perbaikiinaja.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class ReportTest {

    @Test
    void testValidReport() {
        // Arrange
        Report report = new Report();
        report.setRepairDetails("Replaced screen with a new one");
        report.setRepairSummary("Fixed the cracked screen");
        report.setCompletionDateTime(LocalDateTime.now());

        // Act
        boolean isValid = report.isValid();

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testInvalidReport_MissingFields() {
        // Arrange
        Report report = new Report();

        // Act & Assert
        assertFalse(report.isValid());

        // Set only repairDetails
        report.setRepairDetails("Replaced screen with a new one");
        assertFalse(report.isValid());

        // Reset and set only repairSummary
        report = new Report();
        report.setRepairSummary("Fixed the cracked screen");
        assertFalse(report.isValid());

        // Reset and set only completionDateTime
        report = new Report();
        report.setCompletionDateTime(LocalDateTime.now());
        assertFalse(report.isValid());

        // Set repairDetails and repairSummary but not completionDateTime
        report = new Report();
        report.setRepairDetails("Replaced screen with a new one");
        report.setRepairSummary("Fixed the cracked screen");
        assertFalse(report.isValid());

        // Set repairDetails and completionDateTime but not repairSummary
        report = new Report();
        report.setRepairDetails("Replaced screen with a new one");
        report.setCompletionDateTime(LocalDateTime.now());
        assertFalse(report.isValid());

        // Set repairSummary and completionDateTime but not repairDetails
        report = new Report();
        report.setRepairSummary("Fixed the cracked screen");
        report.setCompletionDateTime(LocalDateTime.now());
        assertFalse(report.isValid());
    }

    @Test
    void testInvalidReport_EmptyStrings() {
        // Arrange
        Report report = new Report();
        report.setRepairDetails("");
        report.setRepairSummary("Fixed the cracked screen");
        report.setCompletionDateTime(LocalDateTime.now());

        // Act & Assert
        assertFalse(report.isValid());

        // Reset and set empty repairSummary
        report = new Report();
        report.setRepairDetails("Replaced screen with a new one");
        report.setRepairSummary("");
        report.setCompletionDateTime(LocalDateTime.now());
        assertFalse(report.isValid());
    }
}