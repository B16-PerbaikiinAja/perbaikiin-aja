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

class InProgressStateTest {

    private InProgressState inProgressState;
    private ServiceRequest request;
    private Technician technician;

    @BeforeEach
    void setUp() {
        inProgressState = new InProgressState();
        request = new ServiceRequest();
        request.setState(inProgressState);

        // Create and assign a technician to the request
        technician = new Technician();
        technician.setFullName("Test Technician");
        technician.setCompletedJobs(5);
        technician.setTotalEarnings(1000.0);
        request.setTechnician(technician);

        // Set up an estimate for testing
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.setEstimate(estimate);
    }

    @Test
    void getStateType_ReturnsInProgress() {
        // Act
        ServiceRequestStateType stateType = inProgressState.getStateType();

        // Assert
        assertEquals(ServiceRequestStateType.IN_PROGRESS, stateType);
    }

    @Test
    void completeService_WithTechnician_TransitionsToCompletedState() {
        // Act
        ServiceRequestState nextState = inProgressState.completeService(request);

        // Assert
        assertTrue(nextState instanceof CompletedState);
    }

    @Test
    void completeService_UpdatesTechnicianStats() {
        // Arrange
        int initialCompletedJobs = technician.getCompletedJobCount();
        double initialEarnings = technician.getTotalEarnings();

        // Act
        inProgressState.completeService(request);

        // Assert
        assertEquals(initialCompletedJobs + 1, technician.getCompletedJobCount());
        assertEquals(initialEarnings + 100.0, technician.getTotalEarnings());
    }

    @Test
    void completeService_WithoutTechnician_ThrowsException() {
        // Arrange
        request.setTechnician(null);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            inProgressState.completeService(request);
        });

        assertTrue(exception.getMessage().contains("no technician assigned"));
    }

    @Test
    void provideEstimate_ThrowsException() {
        // Arrange
        RepairEstimate estimate = mock(RepairEstimate.class);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            inProgressState.provideEstimate(request, estimate);
        });

        assertTrue(exception.getMessage().contains("already in progress"));
    }

    @Test
    void acceptEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            inProgressState.acceptEstimate(request);
        });

        assertTrue(exception.getMessage().contains("already in progress"));
    }

    @Test
    void rejectEstimate_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            inProgressState.rejectEstimate(request);
        });

        assertTrue(exception.getMessage().contains("already in progress"));
    }

    @Test
    void startService_ThrowsException() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            inProgressState.startService(request);
        });

        assertTrue(exception.getMessage().contains("already in progress"));
    }

    @Test
    void createReport_ThrowsException() {
        // Arrange
        Report report = mock(Report.class);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            inProgressState.createReport(request, report);
        });

        assertTrue(exception.getMessage().contains("must be completed first"));
    }
}