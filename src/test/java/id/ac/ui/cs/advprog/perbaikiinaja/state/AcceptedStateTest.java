package id.ac.ui.cs.advprog.perbaikiinaja.state;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class AcceptedStateTest {

    private AcceptedState acceptedState;
    private ServiceRequest request;
    private Technician technician;

    @BeforeEach
    void setUp() {
        acceptedState = new AcceptedState();
        request = new ServiceRequest();
        request.setState(acceptedState);

        // Create and assign a technician to the request
        technician = new Technician();
        technician.setFullName("Test Technician");
        request.setTechnician(technician);

        // Set up an estimate for testing
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.setEstimate(estimate);
    }

    @Test
    void getStateType_ReturnsAccepted() {
        // Act
        ServiceRequestStateType stateType = acceptedState.getStateType();

        // Assert
        assertEquals(ServiceRequestStateType.ACCEPTED, stateType);
    }

    @Test
    void startService_WithTechnician_TransitionsToInProgressState() {
        // Act
        ServiceRequestState nextState = acceptedState.startService(request);

        // Assert
        assertTrue(nextState instanceof InProgressState);
    }

    @Test
    void startService_WithoutTechnician_ThrowsException() {
        // Arrange
        request.setTechnician(null);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            acceptedState.startService(request);
        });

        assertTrue(exception.getMessage().contains("no technician assigned"));
    }

    @Test
    void provideEstimate_ThrowsException() {
        // Arrange
        RepairEstimate estimate = mock(RepairEstimate.class);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            acceptedState.provideEstimate(request, estimate);
        });

        assertTrue(exception.getMessage().contains("already been accepted"));
    }

    @Test
    void acceptEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            acceptedState.acceptEstimate(request);
        });

        assertTrue(exception.getMessage().contains("already been accepted"));
    }

    @Test
    void rejectEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            acceptedState.rejectEstimate(request);
        });

        assertTrue(exception.getMessage().contains("already been accepted"));
    }

    @Test
    void completeService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            acceptedState.completeService(request);
        });

        assertTrue(exception.getMessage().contains("must be started first"));
    }

    @Test
    void createReport_ThrowsException() {
        // Arrange
        Report report = mock(Report.class);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            acceptedState.createReport(request, report);
        });

        assertTrue(exception.getMessage().contains("must be completed first"));
    }
}