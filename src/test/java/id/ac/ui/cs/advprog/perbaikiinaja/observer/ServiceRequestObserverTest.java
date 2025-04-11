package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Item;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

class ServiceRequestObserverTest {

    private ServiceRequestSubject subject;
    private ServiceRequestObserver mockObserver;
    private ServiceRequest request;
    private Customer customer;
    private Technician technician;
    private Item item;
    private RepairEstimate estimate;
    private Report report;

    @BeforeEach
    void setUp() {
        // Create subject and mock observer
        subject = new ServiceRequestSubject();
        mockObserver = Mockito.mock(ServiceRequestObserver.class);
        subject.addObserver(mockObserver);

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
        subject.notifyStateChange(request, "PENDING", "ESTIMATED");
        Mockito.verify(mockObserver).onStateChange(request, "PENDING", "ESTIMATED");

        // Test estimate provided notification
        request.setEstimate(estimate);
        subject.notifyEstimateProvided(request);
        Mockito.verify(mockObserver).onEstimateProvided(request);

        // Test estimate accepted notification
        subject.notifyEstimateAccepted(request);
        Mockito.verify(mockObserver).onEstimateAccepted(request);

        // Test service completed notification
        subject.notifyServiceCompleted(request);
        Mockito.verify(mockObserver).onServiceCompleted(request);

        // Test report created notification
        request.setReport(report);
        subject.notifyReportCreated(request);
        Mockito.verify(mockObserver).onReportCreated(request);
    }

    @Test
    void testMultipleObservers() {
        // Create a second observer
        ServiceRequestObserver mockObserver2 = Mockito.mock(ServiceRequestObserver.class);
        subject.addObserver(mockObserver2);

        // Notify all observers
        subject.notifyStateChange(request, "PENDING", "ESTIMATED");

        // Verify both observers were notified
        Mockito.verify(mockObserver).onStateChange(request, "PENDING", "ESTIMATED");
        Mockito.verify(mockObserver2).onStateChange(request, "PENDING", "ESTIMATED");
    }

    @Test
    void testRemoveObserver() {
        // Remove the observer
        subject.removeObserver(mockObserver);

        // Notify observers
        subject.notifyStateChange(request, "PENDING", "ESTIMATED");

        // Verify the removed observer was not notified
        Mockito.verify(mockObserver, Mockito.never()).onStateChange(request, "PENDING", "ESTIMATED");
    }

    @Test
    void testCustomerNotifier() {
        // Create a real CustomerNotifier
        CustomerNotifier customerNotifier = new CustomerNotifier();

        // No exceptions should be thrown
        customerNotifier.onStateChange(request, "PENDING", "ESTIMATED");
        customerNotifier.onEstimateProvided(request);
        customerNotifier.onEstimateAccepted(request);
        customerNotifier.onEstimateRejected(request);
        customerNotifier.onServiceCompleted(request);
        customerNotifier.onReportCreated(request);
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
}