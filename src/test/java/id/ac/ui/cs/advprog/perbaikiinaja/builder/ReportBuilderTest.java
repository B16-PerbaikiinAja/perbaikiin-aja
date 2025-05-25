package id.ac.ui.cs.advprog.perbaikiinaja.builder;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

class ReportBuilderTest {

    private ReportBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new RepairReportBuilder();
    }

    @Test
    void testBuildValidReport() {
        // Act
        Report report = builder
                .withRepairDetails("Replaced screen with a new one")
                .withRepairSummary("Fixed the cracked screen")
                .completedNow()
                .build();

        // Assert
        assertNotNull(report);
        assertEquals("Replaced screen with a new one", report.getRepairDetails());
        assertEquals("Fixed the cracked screen", report.getRepairSummary());
        assertNotNull(report.getCompletionDateTime());
        assertTrue(report.isValid());
    }

    @Test
    void testBuildValidReport_WithExplicitCompletionTime() {
        // Arrange
        LocalDateTime completionTime = LocalDateTime.now().minusHours(2);

        // Act
        Report report = builder
                .withRepairDetails("Replaced screen with a new one")
                .withRepairSummary("Fixed the cracked screen")
                .withCompletionDateTime(completionTime)
                .build();

        // Assert
        assertNotNull(report);
        assertEquals(completionTime, report.getCompletionDateTime());
        assertTrue(report.isValid());
    }

    @Test
    void testInvalidInputs() {
        // Act & Assert - Null repair details
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.withRepairDetails(null);
        });
        assertTrue(exception.getMessage().contains("Repair details cannot be null"));

        // Act & Assert - Empty repair details
        exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.withRepairDetails("");
        });
        assertTrue(exception.getMessage().contains("Repair details cannot be null or empty"));

        // Act & Assert - Null repair summary
        exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.withRepairSummary(null);
        });
        assertTrue(exception.getMessage().contains("Repair summary cannot be null"));

        // Act & Assert - Empty repair summary
        exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.withRepairSummary("");
        });
        assertTrue(exception.getMessage().contains("Repair summary cannot be null or empty"));

        // Act & Assert - Null completion date/time
        exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.withCompletionDateTime(null);
        });
        assertTrue(exception.getMessage().contains("Completion date/time cannot be null"));

        // Act & Assert - Future completion date/time
        exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.withCompletionDateTime(LocalDateTime.now().plusDays(1));
        });
        assertTrue(exception.getMessage().contains("Completion date/time cannot be in the future"));
    }

    @Test
    void testBuilderReset() {
        // Arrange - Start with a complete report
        builder
                .withRepairDetails("First repair details")
                .withRepairSummary("First repair summary")
                .completedNow();

        // Act - Reset the builder and build a new report
        Report newReport = ((RepairReportBuilder) builder).reset()
                .withRepairDetails("Second repair details")
                .withRepairSummary("Second repair summary")
                .completedNow()
                .build();

        // Assert
        assertEquals("Second repair details", newReport.getRepairDetails());
        assertEquals("Second repair summary", newReport.getRepairSummary());
    }
}