package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Item;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

class ServiceRequestObserverTest {

    private ServiceRequestSubject subject;
    private StateChangeObserver mockStateObserver;
    private EstimateObserver mockEstimateObserver;
    private ServiceCompletionObserver mockCompletionObserver;
    private ReportObserver mockReportObserver;
    private ServiceRequest request;
    private Customer customer;
    private Technician technician;
    private Item item;
    private RepairEstimate estimate;
    private Report report;

    @BeforeEach
    void setUp() {
        // Create subject and mock observers
        subject = new ServiceRequestSubject();

        // Create mock observers for each specialized interface
        mockStateObserver = Mockito.mock(StateChangeObserver.class);
        mockEstimateObserver = Mockito.mock(EstimateObserver.class);
        mockCompletionObserver = Mockito.mock(ServiceCompletionObserver.class);
        mockReportObserver = Mockito.mock(ReportObserver.class);

        // Add all mock observers to the subject
        subject.addObserver(mockStateObserver);
        subject.addObserver(mockEstimateObserver);
        subject.addObserver(mockCompletionObserver);
        subject.addObserver(mockReportObserver);

        // Create test objects
        request = new ServiceRequest();

        customer = new Customer();
        customer.setFullName("John Doe");
        customer.setEmail("john.doe@example.com");

        technician = new Technician();
        technician.setFullName("Tech Smith");
        technician.setEmail("tech.smith@example.com");

        item = new Item();
        item.setName("Smartphone");
        item.setCondition("Cracked screen");

        estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));

        report = new Report();
        report.setRepairDetails("Replaced screen with a new one");
        report.setRepairSummary("Fixed the cracked screen");
        report.setCompletionDateTime(LocalDateTime.now());

        // Set up service request
        request.setCustomer(customer);
        request.setTechnician(technician);
        request.setItem(item);
    }

    @Test
    void testObserverNotifications() {
        // Test state change notification
        subject.notifyStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);
        Mockito.verify(mockStateObserver).onStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);

        // Test estimate provided notification
        request.setEstimate(estimate);
        subject.notifyEstimateProvided(request);
        Mockito.verify(mockEstimateObserver).onEstimateProvided(request);

        // Test estimate accepted notification
        subject.notifyEstimateAccepted(request);
        Mockito.verify(mockEstimateObserver).onEstimateAccepted(request);

        // Test service completed notification
        subject.notifyServiceCompleted(request);
        Mockito.verify(mockCompletionObserver).onServiceCompleted(request);

        // Test report created notification
        request.setReport(report);
        subject.notifyReportCreated(request);
        Mockito.verify(mockReportObserver).onReportCreated(request);
    }

    @Test
    void testMultipleObservers() {
        // Create a second state observer
        StateChangeObserver mockStateObserver2 = Mockito.mock(StateChangeObserver.class);
        subject.addObserver(mockStateObserver2);

        // Notify all observers
        subject.notifyStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);

        // Verify both observers were notified
        Mockito.verify(mockStateObserver).onStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);
        Mockito.verify(mockStateObserver2).onStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);
    }

    @Test
    void testRemoveObserver() {
        // Remove one observer
        subject.removeObserver(mockStateObserver);

        // Notify observers
        subject.notifyStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);

        // Verify the removed observer was not notified
        Mockito.verify(mockStateObserver, Mockito.never()).onStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);
    }

    @Test
    void testCustomerNotifier() {
        // Create a real CustomerNotifier (which implements all observer interfaces)
        CustomerNotifier customerNotifier = new CustomerNotifier();

        // Add it to the subject
        subject.addObserver(customerNotifier);

        // Make sure the service request has an estimate before testing onEstimateProvided
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        request.setEstimate(estimate);

        // Test all notification methods - no exceptions should be thrown
        subject.notifyStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);
        subject.notifyEstimateProvided(request);
        subject.notifyEstimateAccepted(request);
        subject.notifyEstimateRejected(request);
        subject.notifyServiceCompleted(request);

        // For onReportCreated, make sure there's a report
        Report report = new Report();
        report.setRepairSummary("Fixed the screen");
        request.setReport(report);

        subject.notifyReportCreated(request);
    }

    @Test
    void testTechnicianStatsUpdater() {
        // Create a real TechnicianStatsUpdater
        TechnicianStatsUpdater statsUpdater = new TechnicianStatsUpdater();

        // Record initial stats
        int initialJobCount = technician.getCompletedJobCount();
        double initialEarnings = technician.getTotalEarnings();

        // Service completed notification should update stats
        request.setEstimate(estimate);
        statsUpdater.onServiceCompleted(request);

        // Verify stats were updated
        assertEquals(initialJobCount + 1, technician.getCompletedJobCount());
        assertEquals(initialEarnings + estimate.getCost(), technician.getTotalEarnings(), 0.001);
    }

    @Test
    void testObserverImplementingMultipleInterfaces() {
        // Create a mock observer that implements multiple interfaces
        MultipleInterfaceObserver mockMultiObserver = Mockito.mock(MultipleInterfaceObserver.class);
        subject.addObserver(mockMultiObserver);

        // Notify different types of events
        subject.notifyStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);
        subject.notifyServiceCompleted(request);

        // Verify the observer received both notifications
        Mockito.verify(mockMultiObserver).onStateChange(request, ServiceRequestStateType.PENDING, ServiceRequestStateType.ESTIMATED);
        Mockito.verify(mockMultiObserver).onServiceCompleted(request);
    }

    // Interface for testing an observer that implements multiple specialized interfaces
    private interface MultipleInterfaceObserver extends StateChangeObserver, ServiceCompletionObserver {
        // No additional methods needed
    }
}