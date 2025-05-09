package id.ac.ui.cs.advprog.perbaikiinaja.model;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.perbaikiinaja.state.PendingState;
import id.ac.ui.cs.advprog.perbaikiinaja.state.EstimatedState;
import id.ac.ui.cs.advprog.perbaikiinaja.state.AcceptedState;
import id.ac.ui.cs.advprog.perbaikiinaja.state.RejectedState;
import id.ac.ui.cs.advprog.perbaikiinaja.state.InProgressState;
import id.ac.ui.cs.advprog.perbaikiinaja.state.CompletedState;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;

class ServiceRequestTest {

    private ServiceRequest request;
    private Customer customer;
    private Technician technician;
    private Item item;

    @BeforeEach
    void setUp() {
        // Create a service request with required dependencies
        request = new ServiceRequest();

        customer = new Customer();
        customer.setFullName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhoneNumber("1234567890");

        technician = new Technician();
        technician.setFullName("Tech Smith");
        technician.setEmail("tech.smith@example.com");
        technician.setPhoneNumber("0987654321");

        item = new Item();
        item.setName("Smartphone");
        item.setCondition("Cracked screen");
        item.setIssueDescription("Screen doesn't respond to touch");

        request.setCustomer(customer);
        request.setTechnician(technician);
        request.setItem(item);
        request.setProblemDescription("Phone screen is cracked and doesn't respond to touch");
    }

    @Test
    void testInitialState() {
        // Assert
        assertTrue(request.getState() instanceof PendingState);
        assertEquals(ServiceRequestStateType.PENDING, request.getStateType());
    }

    @Test
    void testFullLifecycle_HappyPath() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        estimate.setNotes("Will need to replace the screen");

        Report report = new Report();
        report.setRepairDetails("Replaced the screen with a new one");
        report.setRepairSummary("Fixed the cracked screen");
        report.setCompletionDateTime(LocalDateTime.now());

        // Act & Assert - Full lifecycle

        // Step 1: Provide estimate (PENDING -> ESTIMATED)
        request.provideEstimate(estimate);
        assertTrue(request.getState() instanceof EstimatedState);
        assertEquals(ServiceRequestStateType.ESTIMATED, request.getStateType());
        assertEquals(estimate, request.getEstimate());

        // Step 2: Accept estimate (ESTIMATED -> ACCEPTED)
        request.acceptEstimate();
        assertTrue(request.getState() instanceof AcceptedState);
        assertEquals(ServiceRequestStateType.ACCEPTED, request.getStateType());

        // Step 3: Start service (ACCEPTED -> IN_PROGRESS)
        request.startService();
        assertTrue(request.getState() instanceof InProgressState);
        assertEquals(ServiceRequestStateType.IN_PROGRESS, request.getStateType());

        // Record technician stats before completion
        int initialCompletedJobCount = technician.getCompletedJobCount();
        double initialTotalEarnings = technician.getTotalEarnings();

        // Step 4: Complete service (IN_PROGRESS -> COMPLETED)
        request.completeService();
        assertTrue(request.getState() instanceof CompletedState);
        assertEquals(ServiceRequestStateType.COMPLETED, request.getStateType());

        // Verify technician stats were updated
        assertEquals(initialCompletedJobCount + 1, technician.getCompletedJobCount());
        assertEquals(initialTotalEarnings + estimate.getCost(), technician.getTotalEarnings(), 0.001);

        // Step 5: Create report (still in COMPLETED state)
        request.createReport(report);
        assertTrue(request.getState() instanceof CompletedState); // State doesn't change
        assertEquals(ServiceRequestStateType.COMPLETED, request.getStateType());
        assertEquals(report, request.getReport());
    }

    @Test
    void testRejectedPath() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(500.0); // Expensive estimate that might get rejected
        estimate.setCompletionDate(LocalDate.now().plusDays(7));

        // Act
        request.provideEstimate(estimate);
        assertTrue(request.getState() instanceof EstimatedState);

        request.rejectEstimate();
        assertTrue(request.getState() instanceof RejectedState);
        assertEquals(ServiceRequestStateType.REJECTED, request.getStateType());

        // Assert - Cannot do anything with a rejected request
        assertThrows(IllegalStateException.class, () -> request.provideEstimate(estimate));
        assertThrows(IllegalStateException.class, () -> request.acceptEstimate());
        assertThrows(IllegalStateException.class, () -> request.rejectEstimate());
        assertThrows(IllegalStateException.class, () -> request.startService());
        assertThrows(IllegalStateException.class, () -> request.completeService());
        assertThrows(IllegalStateException.class, () -> request.createReport(new Report()));
    }

    @Test
    void testInvalidStateTransitions() {
        // Test invalid actions in PENDING state
        assertThrows(IllegalStateException.class, () -> request.acceptEstimate());
        assertThrows(IllegalStateException.class, () -> request.rejectEstimate());
        assertThrows(IllegalStateException.class, () -> request.startService());
        assertThrows(IllegalStateException.class, () -> request.completeService());

        // Provide an estimate to move to ESTIMATED state
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.provideEstimate(estimate);

        // Test invalid actions in ESTIMATED state
        assertThrows(IllegalStateException.class, () -> request.startService());
        assertThrows(IllegalStateException.class, () -> request.completeService());

        // Accept the estimate to move to ACCEPTED state
        request.acceptEstimate();

        // Test invalid actions in ACCEPTED state
        assertThrows(IllegalStateException.class, () -> request.provideEstimate(estimate));
        assertThrows(IllegalStateException.class, () -> request.acceptEstimate());
        assertThrows(IllegalStateException.class, () -> request.rejectEstimate());
        assertThrows(IllegalStateException.class, () -> request.completeService());

        // Start the service to move to IN_PROGRESS state
        request.startService();

        // Test invalid actions in IN_PROGRESS state
        assertThrows(IllegalStateException.class, () -> request.provideEstimate(estimate));
        assertThrows(IllegalStateException.class, () -> request.acceptEstimate());
        assertThrows(IllegalStateException.class, () -> request.rejectEstimate());
        assertThrows(IllegalStateException.class, () -> request.startService());

        // Complete the service to move to COMPLETED state
        request.completeService();

        // Test invalid actions in COMPLETED state
        assertThrows(IllegalStateException.class, () -> request.provideEstimate(estimate));
        assertThrows(IllegalStateException.class, () -> request.acceptEstimate());
        assertThrows(IllegalStateException.class, () -> request.rejectEstimate());
        assertThrows(IllegalStateException.class, () -> request.startService());
        assertThrows(IllegalStateException.class, () -> request.completeService());
    }
}