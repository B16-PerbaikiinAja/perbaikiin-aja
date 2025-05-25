package id.ac.ui.cs.advprog.perbaikiinaja.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import id.ac.ui.cs.advprog.perbaikiinaja.state.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

class ServiceRequestTest {

    private ServiceRequest request;
    private Customer customer;
    private Technician technician;
    private Item item;
    private PaymentMethod paymentMethod;
    private Coupon coupon;

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

        paymentMethod = new PaymentMethod();
        paymentMethod.setName("Credit Card");
        paymentMethod.setProvider("Visa");

        coupon = mock(Coupon.class);
        when(coupon.getDiscountValue()).thenReturn(0.1); // 10% discount

        request.setCustomer(customer);
        request.setTechnician(technician);
        request.setItem(item);
        request.setProblemDescription("Phone screen is cracked and doesn't respond to touch");
        request.setServiceDate(LocalDate.of(2024, 5, 1));
        request.setPaymentMethod(paymentMethod);
        request.setCoupon(coupon);
    }

    @Test
    void testInitialState() {
        // Assert
        assertTrue(request.getState() instanceof PendingState);
        assertEquals(ServiceRequestStateType.PENDING, request.getStateType());
        assertEquals(LocalDate.now(), request.getRequestDate());
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
        Report report = new Report();
        assertThrows(IllegalStateException.class, () -> request.createReport(report));
    }

    @Test
    void testInvalidStateTransitions() {
        // Test invalid actions in PENDING state
        assertThrows(IllegalStateException.class, () -> request.acceptEstimate());
        assertThrows(IllegalStateException.class, () -> request.rejectEstimate());
        assertThrows(IllegalStateException.class, () -> request.startService());
        assertThrows(IllegalStateException.class, () -> request.completeService());
        Report report2 = new Report();
        assertThrows(IllegalStateException.class, () -> request.createReport(report2));

        // Provide an estimate to move to ESTIMATED state
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.provideEstimate(estimate);

        // Test invalid actions in ESTIMATED state
        assertThrows(IllegalStateException.class, () -> request.startService());
        assertThrows(IllegalStateException.class, () -> request.completeService());
        Report report = new Report();
        assertThrows(IllegalStateException.class, () -> request.createReport(report));

        // Accept the estimate to move to ACCEPTED state
        request.acceptEstimate();

        // Test invalid actions in ACCEPTED state
        assertThrows(IllegalStateException.class, () -> request.provideEstimate(estimate));
        assertThrows(IllegalStateException.class, () -> request.acceptEstimate());
        assertThrows(IllegalStateException.class, () -> request.rejectEstimate());
        assertThrows(IllegalStateException.class, () -> request.completeService());
        Report report1 = new Report();
        assertThrows(IllegalStateException.class, () -> request.createReport(report1));

        // Start the service to move to IN_PROGRESS state
        request.startService();

        // Test invalid actions in IN_PROGRESS state
        assertThrows(IllegalStateException.class, () -> request.provideEstimate(estimate));
        assertThrows(IllegalStateException.class, () -> request.acceptEstimate());
        assertThrows(IllegalStateException.class, () -> request.rejectEstimate());
        assertThrows(IllegalStateException.class, () -> request.startService());
        Report newReport = new Report();
        assertThrows(IllegalStateException.class, () -> request.createReport(newReport));

        // Complete the service to move to COMPLETED state
        request.completeService();

        // Test invalid actions in COMPLETED state
        assertThrows(IllegalStateException.class, () -> request.provideEstimate(estimate));
        assertThrows(IllegalStateException.class, () -> request.acceptEstimate());
        assertThrows(IllegalStateException.class, () -> request.rejectEstimate());
        assertThrows(IllegalStateException.class, () -> request.startService());
        assertThrows(IllegalStateException.class, () -> request.completeService());
    }

    // NEW TESTS TO INCREASE COVERAGE

    @Test
    void testGettersAndSetters() {
        // Test getters
        assertEquals(customer, request.getCustomer());
        assertEquals(technician, request.getTechnician());
        assertEquals(item, request.getItem());
        assertEquals("Phone screen is cracked and doesn't respond to touch", request.getProblemDescription());
        assertEquals(LocalDate.of(2024, 5, 1), request.getServiceDate());
        assertEquals(paymentMethod, request.getPaymentMethod());
        assertEquals(coupon, request.getCoupon());
        // Note: We don't test getId() since it would be null in test environment
        // as ID generation happens in the database/persistence layer

        // Create and set a report to test getReport
        Report report = new Report();
        report.setRepairSummary("Test summary");
        request.setReport(report);
        assertEquals(report, request.getReport());

        // Create and set a new estimate to test getEstimate
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(150.0);
        request.setEstimate(estimate);
        assertEquals(estimate, request.getEstimate());
    }

    @ParameterizedTest
    @EnumSource(ServiceRequestStateType.class)
    void testSetStateType(ServiceRequestStateType stateType) {
        // Set the state type
        request.setStateType(stateType);

        // Verify state type is set correctly
        assertEquals(stateType, request.getStateType());

        // Verify state object is created correctly
        ServiceRequestState state = request.getState();
        assertEquals(stateType, state.getStateType());
    }

    @Test
    void testSetState() {
        // Create different state objects
        ServiceRequestState pendingState = new PendingState();
        ServiceRequestState estimatedState = new EstimatedState();
        ServiceRequestState acceptedState = new AcceptedState();
        ServiceRequestState inProgressState = new InProgressState();
        ServiceRequestState completedState = new CompletedState();
        ServiceRequestState rejectedState = new RejectedState();

        // Test setting each state
        request.setState(pendingState);
        assertEquals(ServiceRequestStateType.PENDING, request.getStateType());
        assertEquals(pendingState.getStateType(), request.getState().getStateType());

        request.setState(estimatedState);
        assertEquals(ServiceRequestStateType.ESTIMATED, request.getStateType());
        assertEquals(estimatedState.getStateType(), request.getState().getStateType());

        request.setState(acceptedState);
        assertEquals(ServiceRequestStateType.ACCEPTED, request.getStateType());
        assertEquals(acceptedState.getStateType(), request.getState().getStateType());

        request.setState(inProgressState);
        assertEquals(ServiceRequestStateType.IN_PROGRESS, request.getStateType());
        assertEquals(inProgressState.getStateType(), request.getState().getStateType());

        request.setState(completedState);
        assertEquals(ServiceRequestStateType.COMPLETED, request.getStateType());
        assertEquals(completedState.getStateType(), request.getState().getStateType());

        request.setState(rejectedState);
        assertEquals(ServiceRequestStateType.REJECTED, request.getStateType());
        assertEquals(rejectedState.getStateType(), request.getState().getStateType());
    }

    @Test
    void testStateSynchronization() {
        // Test that stateType is synchronized when state is changed

        // Start with PENDING state
        assertEquals(ServiceRequestStateType.PENDING, request.getStateType());

        // Set state to ESTIMATED
        ServiceRequestState estimatedState = new EstimatedState();
        request.setState(estimatedState);

        // Verify stateType is updated
        assertEquals(ServiceRequestStateType.ESTIMATED, request.getStateType());
        assertEquals(estimatedState.getStateType(), request.getState().getStateType());

        // Set state to COMPLETED
        ServiceRequestState completedState = new CompletedState();
        request.setState(completedState);

        // Verify stateType is updated
        assertEquals(ServiceRequestStateType.COMPLETED, request.getStateType());
        assertEquals(completedState.getStateType(), request.getState().getStateType());
    }

    @Test
    void testInitializeStateMethod() {
        // Create a new request and set its state type to a different value
        ServiceRequest newRequest = new ServiceRequest();
        newRequest.setStateType(ServiceRequestStateType.COMPLETED);

        // Verify the state was created correctly
        ServiceRequestState state = newRequest.getState();
        assertNotNull(state);
        assertTrue(state instanceof CompletedState);
        assertEquals(ServiceRequestStateType.COMPLETED, state.getStateType());
    }

    @Test
    void testCreateStateFromTypeWithAllTypes() {
        // This tests a private method indirectly through its effects

        // Test PENDING
        request.setStateType(ServiceRequestStateType.PENDING);
        assertTrue(request.getState() instanceof PendingState);

        // Test ESTIMATED
        request.setStateType(ServiceRequestStateType.ESTIMATED);
        assertTrue(request.getState() instanceof EstimatedState);

        // Test ACCEPTED
        request.setStateType(ServiceRequestStateType.ACCEPTED);
        assertTrue(request.getState() instanceof AcceptedState);

        // Test IN_PROGRESS
        request.setStateType(ServiceRequestStateType.IN_PROGRESS);
        assertTrue(request.getState() instanceof InProgressState);

        // Test COMPLETED
        request.setStateType(ServiceRequestStateType.COMPLETED);
        assertTrue(request.getState() instanceof CompletedState);

        // Test REJECTED
        request.setStateType(ServiceRequestStateType.REJECTED);
        assertTrue(request.getState() instanceof RejectedState);
    }

    @Test
    void testEstimateUpdateInEstimatedState() {
        // First transition to ESTIMATED state
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.provideEstimate(estimate);

        // Now provide a new estimate while already in ESTIMATED state
        RepairEstimate updatedEstimate = new RepairEstimate();
        updatedEstimate.setCost(150.0);
        updatedEstimate.setCompletionDate(LocalDate.now().plusDays(5));
        request.provideEstimate(updatedEstimate);

        // Verify we're still in ESTIMATED state but with the new estimate
        assertTrue(request.getState() instanceof EstimatedState);
        assertEquals(ServiceRequestStateType.ESTIMATED, request.getStateType());
        assertEquals(updatedEstimate, request.getEstimate());
        assertEquals(150.0, request.getEstimate().getCost());
        assertEquals(LocalDate.now().plusDays(5), request.getEstimate().getCompletionDate());
    }

    @Test
    void testRequiredTechnicianForStateTransitions() {
        // Create a request without a technician
        ServiceRequest noTechRequest = new ServiceRequest();
        noTechRequest.setCustomer(customer);
        noTechRequest.setItem(item);

        // Transition to ESTIMATED state
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        noTechRequest.provideEstimate(estimate);

        // Accept the estimate
        noTechRequest.acceptEstimate();

        // Try to start service without technician - should throw exception
        assertThrows(IllegalStateException.class, noTechRequest::startService);

        // Now assign a technician
        noTechRequest.setTechnician(technician);

        // Should be able to start service now
        noTechRequest.startService();
        assertTrue(noTechRequest.getState() instanceof InProgressState);
    }

    @Test
    void testStateRecreation() {
        // Test that getState() creates a new state object if needed
        // We'll modify the state type and verify a new state is created
        request.setStateType(ServiceRequestStateType.PENDING);

        // Change state type to trigger recreation
        request.setStateType(ServiceRequestStateType.COMPLETED);

        // Get state and verify it's correct type
        ServiceRequestState state = request.getState();
        assertNotNull(state);
        assertTrue(state instanceof CompletedState);
        assertEquals(ServiceRequestStateType.COMPLETED, state.getStateType());
    }
}