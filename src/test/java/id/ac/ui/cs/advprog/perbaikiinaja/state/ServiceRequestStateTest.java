package id.ac.ui.cs.advprog.perbaikiinaja.state;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Technician;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;

import java.time.LocalDate;

class ServiceRequestStateTest {

    @Test
    void pendingState_CanTransitionToEstimated_WhenEstimateProvided() {
        // Arrange
        ServiceRequest request = new ServiceRequest();
        PendingState pendingState = new PendingState();
        request.setState(pendingState);

        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(java.time.LocalDate.now().plusDays(3));

        // Act
        ServiceRequestState nextState = pendingState.provideEstimate(request, estimate);
        request.setState(nextState);

        // Assert
        assertTrue(nextState instanceof EstimatedState);
        assertEquals(estimate, request.getEstimate());
    }

    @Test
    void pendingState_CannotBeCompleted() {
        // Arrange
        ServiceRequest request = new ServiceRequest();
        PendingState pendingState = new PendingState();
        request.setState(pendingState);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            pendingState.completeService(request);
        });

        assertTrue(exception.getMessage().contains("cannot be completed"));
    }

    @Test
    void estimatedState_CanTransitionToAccepted_WhenCustomerAcceptsEstimate() {
        // Arrange
        ServiceRequest request = new ServiceRequest();
        EstimatedState estimatedState = new EstimatedState();
        request.setState(estimatedState);

        // Add an estimate to the request
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.setEstimate(estimate);

        // Act
        ServiceRequestState nextState = estimatedState.acceptEstimate(request);
        request.setState(nextState);

        // Assert
        assertTrue(nextState instanceof AcceptedState);
    }

    @Test
    void estimatedState_CanTransitionToRejected_WhenCustomerRejectsEstimate() {
        // Arrange
        ServiceRequest request = new ServiceRequest();
        EstimatedState estimatedState = new EstimatedState();
        request.setState(estimatedState);

        // Add an estimate to the request
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.setEstimate(estimate);

        // Act
        ServiceRequestState nextState = estimatedState.rejectEstimate(request);
        request.setState(nextState);

        // Assert
        assertTrue(nextState instanceof RejectedState);
    }

    @Test
    void acceptedState_CanTransitionToInProgress() {
        // Arrange
        ServiceRequest request = new ServiceRequest();
        AcceptedState acceptedState = new AcceptedState();
        request.setState(acceptedState);

        // Assign a technician to the request
        Technician technician = new Technician();
        technician.setFullName("Tech Smith");
        request.setTechnician(technician);

        // Act
        ServiceRequestState nextState = acceptedState.startService(request);
        request.setState(nextState);

        // Assert
        assertTrue(nextState instanceof InProgressState);
    }

    @Test
    void inProgressState_CanTransitionToCompleted() {
        // Arrange
        ServiceRequest request = new ServiceRequest();
        InProgressState inProgressState = new InProgressState();
        request.setState(inProgressState);

        // Assign a technician to the request
        Technician technician = new Technician();
        technician.setFullName("Tech Smith");
        request.setTechnician(technician);

        // Add an estimate for calculating earnings
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.setEstimate(estimate);

        // Act
        ServiceRequestState nextState = inProgressState.completeService(request);
        request.setState(nextState);

        // Assert
        assertTrue(nextState instanceof CompletedState);
    }

    @Test
    void rejectedState_IsTerminal() {
        // Arrange
        ServiceRequest request = new ServiceRequest();
        RejectedState rejectedState = new RejectedState();
        request.setState(rejectedState);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            rejectedState.acceptEstimate(request);
        });

        assertTrue(exception.getMessage().contains("rejected state cannot be changed"));
    }

    @Test
    void completedState_IsTerminal() {
        // Arrange
        ServiceRequest request = new ServiceRequest();
        CompletedState completedState = new CompletedState();
        request.setState(completedState);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            completedState.startService(request);
        });

        assertTrue(exception.getMessage().contains("completed state cannot be changed"));
    }
}