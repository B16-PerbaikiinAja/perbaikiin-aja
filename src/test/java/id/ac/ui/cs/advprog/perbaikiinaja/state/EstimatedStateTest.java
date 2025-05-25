package id.ac.ui.cs.advprog.perbaikiinaja.state;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

class EstimatedStateTest {

    private EstimatedState estimatedState;
    private ServiceRequest request;

    @Mock
    private RepairEstimate mockEstimate;

    @Mock
    private Report mockReport;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        estimatedState = new EstimatedState();
        request = new ServiceRequest();
        request.setState(estimatedState);

        // Set up a valid estimate for testing
        RepairEstimate validEstimate = new RepairEstimate();
        validEstimate.setCost(100.0);
        validEstimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.setEstimate(validEstimate);
    }

    @Test
    void getStateType_ReturnsEstimated() {
        // Act
        ServiceRequestStateType stateType = estimatedState.getStateType();

        // Assert
        assertEquals(ServiceRequestStateType.ESTIMATED, stateType);
    }

    @Test
    void provideEstimate_WithValidEstimate_StaysInEstimatedState() {
        // Arrange
        when(mockEstimate.isValid()).thenReturn(true);

        // Act
        ServiceRequestState nextState = estimatedState.provideEstimate(request, mockEstimate);

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
            estimatedState.provideEstimate(request, mockEstimate);
        });

        assertTrue(exception.getMessage().contains("Estimate must be valid"));
    }

    @Test
    void provideEstimate_WithNullEstimate_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            estimatedState.provideEstimate(request, null);
        });

        assertTrue(exception.getMessage().contains("Estimate must be valid"));
    }

    @Test
    void acceptEstimate_TransitionsToAcceptedState() {
        // Act
        ServiceRequestState nextState = estimatedState.acceptEstimate(request);

        // Assert
        assertTrue(nextState instanceof AcceptedState);
    }

    @Test
    void acceptEstimate_WithNoEstimate_ThrowsException() {
        // Arrange
        ServiceRequest requestWithoutEstimate = new ServiceRequest();
        requestWithoutEstimate.setState(estimatedState);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            estimatedState.acceptEstimate(requestWithoutEstimate);
        });

        assertTrue(exception.getMessage().contains("no estimate found"));
    }

    @Test
    void rejectEstimate_TransitionsToRejectedState() {
        // Act
        ServiceRequestState nextState = estimatedState.rejectEstimate(request);

        // Assert
        assertTrue(nextState instanceof RejectedState);
    }

    @Test
    void rejectEstimate_WithNoEstimate_ThrowsException() {
        // Arrange
        ServiceRequest requestWithoutEstimate = new ServiceRequest();
        requestWithoutEstimate.setState(estimatedState);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            estimatedState.rejectEstimate(requestWithoutEstimate);
        });

        assertTrue(exception.getMessage().contains("no estimate found"));
    }

    @Test
    void startService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            estimatedState.startService(request);
        });

        assertTrue(exception.getMessage().contains("estimate must be accepted first"));
    }

    @Test
    void completeService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            estimatedState.completeService(request);
        });

        assertTrue(exception.getMessage().contains("estimate must be accepted and service started first"));
    }

    @Test
    void createReport_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            estimatedState.createReport(request, mockReport);
        });

        assertTrue(exception.getMessage().contains("service must be completed first"));
    }
}