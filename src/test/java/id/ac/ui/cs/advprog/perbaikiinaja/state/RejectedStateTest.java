package id.ac.ui.cs.advprog.perbaikiinaja.state;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RejectedStateTest {

    private RejectedState rejectedState;
    private ServiceRequest request;

    @BeforeEach
    void setUp() {
        rejectedState = new RejectedState();
        request = new ServiceRequest();
        request.setState(rejectedState);
    }

    @Test
    void getStateType_ReturnsRejected() {
        // Act
        ServiceRequestStateType stateType = rejectedState.getStateType();

        // Assert
        assertEquals(ServiceRequestStateType.REJECTED, stateType);
    }

    @Test
    void provideEstimate_ThrowsException() {
        // Arrange
        RepairEstimate estimate = mock(RepairEstimate.class);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rejectedState.provideEstimate(request, estimate);
        });

        assertTrue(exception.getMessage().contains("request in rejected state cannot be changed"));
    }

    @Test
    void acceptEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rejectedState.acceptEstimate(request);
        });

        assertTrue(exception.getMessage().contains("request in rejected state cannot be changed"));
    }

    @Test
    void rejectEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rejectedState.rejectEstimate(request);
        });

        assertTrue(exception.getMessage().contains("request in rejected state cannot be changed"));
    }

    @Test
    void startService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rejectedState.startService(request);
        });

        assertTrue(exception.getMessage().contains("request in rejected state cannot be changed"));
    }

    @Test
    void completeService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rejectedState.completeService(request);
        });

        assertTrue(exception.getMessage().contains("request in rejected state cannot be changed"));
    }

    @Test
    void createReport_ThrowsException() {
        // Arrange
        Report report = mock(Report.class);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rejectedState.createReport(request, report);
        });

        assertTrue(exception.getMessage().contains("request in rejected state cannot be changed"));
    }
}