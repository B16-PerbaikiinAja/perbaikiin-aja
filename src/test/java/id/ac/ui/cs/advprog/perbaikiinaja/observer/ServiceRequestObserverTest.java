package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Item;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

class ServiceRequestObserverTest {

    private ServiceRequestSubject subject;
    private ServiceCompletionObserver mockCompletionObserver;
    private ServiceRequest request;
    private Customer customer;
    private Technician technician;
    private Item item;
    private RepairEstimate estimate;

    @BeforeEach
    void setUp() {
        // Create subject and mock observers
        subject = new ServiceRequestSubject();

        // Create mock observer for service completion
        mockCompletionObserver = Mockito.mock(ServiceCompletionObserver.class);

        // Add the mock observer to the subject
        subject.addObserver(mockCompletionObserver);

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

        // Set up service request
        request.setCustomer(customer);
        request.setTechnician(technician);
        request.setItem(item);
        request.setEstimate(estimate);
    }

    @Test
    void testCompletionObserverNotification() {
        // Test service completed notification
        subject.notifyServiceCompleted(request);
        Mockito.verify(mockCompletionObserver).onServiceCompleted(request);
    }

    @Test
    void testMultipleObservers() {
        // Create a second completion observer
        ServiceCompletionObserver mockCompletionObserver2 = Mockito.mock(ServiceCompletionObserver.class);
        subject.addObserver(mockCompletionObserver2);

        // Notify all observers
        subject.notifyServiceCompleted(request);

        // Verify both observers were notified
        Mockito.verify(mockCompletionObserver).onServiceCompleted(request);
        Mockito.verify(mockCompletionObserver2).onServiceCompleted(request);
    }

    @Test
    void testRemoveObserver() {
        // Remove one observer
        subject.removeObserver(mockCompletionObserver);

        // Notify observers
        subject.notifyServiceCompleted(request);

        // Verify the removed observer was not notified
        Mockito.verify(mockCompletionObserver, Mockito.never()).onServiceCompleted(request);
    }

    @Test
    void testTechnicianStatsUpdater() {
        // Create a real TechnicianStatsUpdater
        TechnicianStatsUpdater statsUpdater = new TechnicianStatsUpdater();

        // Record initial stats
        int initialJobCount = technician.getCompletedJobCount();
        double initialEarnings = technician.getTotalEarnings();

        // Service completed notification should update stats
        statsUpdater.onServiceCompleted(request);

        // Verify stats were updated
        assertEquals(initialJobCount + 1, technician.getCompletedJobCount());
        assertEquals(initialEarnings + estimate.getCost(), technician.getTotalEarnings(), 0.001);
    }

    @Test
    void testNullObserverHandling() {
        // Create a fresh subject instance to avoid interference from setUp() method
        ServiceRequestSubject freshSubject = new ServiceRequestSubject();

        // Create a mock observer to verify it's still notified properly
        ServiceCompletionObserver mockObserver = Mockito.mock(ServiceCompletionObserver.class);
        freshSubject.addObserver(mockObserver);

        // Adding null observer should not throw exception
        freshSubject.addObserver(null);

        // Should be able to notify without error
        freshSubject.notifyServiceCompleted(request);

        // Assert that the valid observer was still notified despite the null observer
        Mockito.verify(mockObserver).onServiceCompleted(request);

        // Assert that the observer list size is 1 (null shouldn't be added)
        int observerCount = getObserverListSize(freshSubject);
        assertEquals(1, observerCount, "Only one valid observer should be in the list");
    }

    // Helper method to get the observer list size using reflection
    private int getObserverListSize(ServiceRequestSubject subject) {
        try {
            java.lang.reflect.Field field = ServiceRequestSubject.class.getDeclaredField("completionObservers");
            field.setAccessible(true);
            List<?> observers = (List<?>) field.get(subject);
            return observers.size();
        } catch (Exception e) {
            fail("Could not access completionObservers field: " + e.getMessage());
            return -1;
        }
    }
}