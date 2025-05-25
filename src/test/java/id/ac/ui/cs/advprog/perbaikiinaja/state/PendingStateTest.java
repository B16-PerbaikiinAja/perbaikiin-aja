package id.ac.ui.cs.advprog.perbaikiinaja.state;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

class PendingStateTest {

    private PendingState pendingState;
    private ServiceRequest request;

    @Mock
    private RepairEstimate mockEstimate;

    @Mock
    private Report mockReport;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pendingState = new PendingState();
        request = new ServiceRequest();
        request.setState(pendingState);
    }

    @Test
    void getStateType_ReturnsPending() {
        // Act
        ServiceRequestStateType stateType = pendingState.getStateType();

        // Assert
        assertEquals(ServiceRequestStateType.PENDING, stateType);
    }

    @Test
    void provideEstimate_WithValidEstimate_TransitionsToEstimatedState() {
        // Arrange
        when(mockEstimate.isValid()).thenReturn(true);

        // Act
        ServiceRequestState nextState = pendingState.provideEstimate(request, mockEstimate);

        // Assert
        assertTrue(nextState instanceof EstimatedState);
        assertEquals(mockEstimate, request.getEstimate());
    }

    @Test
    void provideEstimate_WithInvalidEstimate_ThrowsException() {
        // Arrange
        when(mockEstimate.isValid()).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pendingState.provideEstimate(request, mockEstimate);
        });

        assertTrue(exception.getMessage().contains("Estimate must be valid"));
    }

    @Test
    void provideEstimate_WithNullEstimate_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pendingState.provideEstimate(request, null);
        });

        assertTrue(exception.getMessage().contains("Estimate must be valid"));
    }

    @Test
    void provideEstimate_WithRealEstimate_TransitionsToEstimatedState() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));

        // Act
        ServiceRequestState nextState = pendingState.provideEstimate(request, estimate);

        // Assert
        assertTrue(nextState instanceof EstimatedState);
        assertEquals(estimate, request.getEstimate());
    }

    @Test
    void acceptEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pendingState.acceptEstimate(request);
        });

        assertTrue(exception.getMessage().contains("no estimate has been provided yet"));
    }

    @Test
    void rejectEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pendingState.rejectEstimate(request);
        });

        assertTrue(exception.getMessage().contains("no estimate has been provided yet"));
    }

    @Test
    void startService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pendingState.startService(request);
        });

        assertTrue(exception.getMessage().contains("estimate must be provided and accepted first"));
    }

    @Test
    void completeService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pendingState.completeService(request);
        });

        assertTrue(exception.getMessage().contains("estimate must be provided and accepted first"));
    }

    @Test
    void createReport_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pendingState.createReport(request, mockReport);
        });

        assertTrue(exception.getMessage().contains("service must be completed first"));
    }
}