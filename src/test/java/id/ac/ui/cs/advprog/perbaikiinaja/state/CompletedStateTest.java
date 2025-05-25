package id.ac.ui.cs.advprog.perbaikiinaja.state;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CompletedStateTest {

    private CompletedState completedState;
    private ServiceRequest request;

    @Mock
    private RepairEstimate mockEstimate;

    @Mock
    private Report mockReport;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        completedState = new CompletedState();
        request = new ServiceRequest();
        request.setState(completedState);
    }

    @Test
    void getStateType_ReturnsCompleted() {
        // Act
        ServiceRequestStateType stateType = completedState.getStateType();

        // Assert
        assertEquals(ServiceRequestStateType.COMPLETED, stateType);
    }

    @Test
    void createReport_WithValidReport_SetsReportOnRequest() {
        // Arrange
        when(mockReport.isValid()).thenReturn(true);

        // Act
        completedState.createReport(request, mockReport);

        // Assert
        assertEquals(mockReport, request.getReport());
    }

    @Test
    void createReport_WithInvalidReport_ThrowsException() {
        // Arrange
        when(mockReport.isValid()).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            completedState.createReport(request, mockReport);
        });

        assertTrue(exception.getMessage().contains("Report must be valid"));
    }

    @Test
    void createReport_WithNullReport_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            completedState.createReport(request, null);
        });

        assertTrue(exception.getMessage().contains("Report must be valid"));
    }

    @Test
    void provideEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            completedState.provideEstimate(request, mockEstimate);
        });

        assertTrue(exception.getMessage().contains("service has already been completed"));
    }

    @Test
    void acceptEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            completedState.acceptEstimate(request);
        });

        assertTrue(exception.getMessage().contains("service has already been completed"));
    }

    @Test
    void rejectEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            completedState.rejectEstimate(request);
        });

        assertTrue(exception.getMessage().contains("service has already been completed"));
    }

    @Test
    void startService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            completedState.startService(request);
        });

        assertTrue(exception.getMessage().contains("completed state cannot be changed"));
    }

    @Test
    void completeService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            completedState.completeService(request);
        });

        assertTrue(exception.getMessage().contains("service has already been completed"));
    }
}