package id.ac.ui.cs.advprog.perbaikiinaja.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Item;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.CustomerRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.TechnicianRepository;

class ServiceRequestServiceTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private UserRepository userRepository;

    private ServiceRequestService serviceRequestService;

    private UUID customerId;
    private UUID technicianId;
    private UUID requestId;
    private Customer customer;
    private Technician technician;
    private ServiceRequest serviceRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create service with mocked repositories
        serviceRequestService = new ServiceRequestServiceImpl(
                serviceRequestRepository,
                userRepository);

        // Create test data
        customer = new Customer();
        customer.setFullName("John Doe");
        customer.setEmail("john.doe@example.com");

        technician = new Technician();
        technician.setFullName("Tech Smith");
        technician.setEmail("tech.smith@example.com");

        // Get the IDs from the actual objects
        customerId = customer.getId();
        technicianId = technician.getId();

        Item item = new Item();
        item.setName("Smartphone");
        item.setCondition("Cracked screen");

        serviceRequest = new ServiceRequest();
        serviceRequest.setCustomer(customer);
        serviceRequest.setTechnician(technician);
        serviceRequest.setItem(item);
        serviceRequest.setProblemDescription("Screen is cracked and not responding to touch");

        requestId = serviceRequest.getId();

        // Mock repository method behaviors
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technician));
        when(serviceRequestRepository.findById(requestId)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void testFindByTechnician() {
        // Arrange
        ServiceRequest request1 = new ServiceRequest();
        ServiceRequest request2 = new ServiceRequest();
        List<ServiceRequest> expectedRequests = Arrays.asList(request1, request2);

        when(serviceRequestRepository.findByTechnicianId(technicianId)).thenReturn(expectedRequests);

        // Act
        List<ServiceRequest> actualRequests = serviceRequestService.findByTechnician(technicianId);

        // Assert
        assertEquals(expectedRequests, actualRequests);
        verify(serviceRequestRepository).findByTechnicianId(technicianId);
    }

    @Test
    void testFindByCustomer() {
        // Arrange
        ServiceRequest request1 = new ServiceRequest();
        ServiceRequest request2 = new ServiceRequest();
        List<ServiceRequest> expectedRequests = Arrays.asList(request1, request2);

        when(serviceRequestRepository.findByCustomerId(customerId)).thenReturn(expectedRequests);

        // Act
        List<ServiceRequest> actualRequests = serviceRequestService.findByCustomer(customerId);

        // Assert
        assertEquals(expectedRequests, actualRequests);
        verify(serviceRequestRepository).findByCustomerId(customerId);
    }

    @Test
    void testFindById() {
        // Act
        Optional<ServiceRequest> result = serviceRequestService.findById(requestId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(serviceRequest, result.get());
        verify(serviceRequestRepository).findById(requestId);
    }

    @Test
    void testProvideEstimate() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));

        // Act
        ServiceRequest updatedRequest = serviceRequestService.provideEstimate(requestId, estimate, technicianId);

        // Assert
        assertNotNull(updatedRequest);
        assertEquals(estimate, updatedRequest.getEstimate());
        assertEquals("ESTIMATED", updatedRequest.getStateName());
        verify(serviceRequestRepository).findById(requestId);
        verify(technicianRepository).findById(technicianId);
        verify(serviceRequestRepository).save(updatedRequest);
    }

    @Test
    void testAcceptEstimate() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        serviceRequest.provideEstimate(estimate); // Move to ESTIMATED state

        // Act
        ServiceRequest updatedRequest = serviceRequestService.acceptEstimate(requestId, customerId);

        // Assert
        assertNotNull(updatedRequest);
        assertEquals("ACCEPTED", updatedRequest.getStateName());
        verify(serviceRequestRepository).findById(requestId);
        verify(customerRepository).findById(customerId);
        verify(serviceRequestRepository).save(updatedRequest);
    }

    @Test
    void testRejectEstimate() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        serviceRequest.provideEstimate(estimate); // Move to ESTIMATED state

        // Act
        ServiceRequest updatedRequest = serviceRequestService.rejectEstimate(requestId, customerId);

        // Assert
        assertNotNull(updatedRequest);
        assertEquals("REJECTED", updatedRequest.getStateName());
        verify(serviceRequestRepository).findById(requestId);
        verify(customerRepository).findById(customerId);
        verify(serviceRequestRepository).save(updatedRequest);
    }

    @Test
    void testStartService() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        serviceRequest.provideEstimate(estimate); // Move to ESTIMATED state
        serviceRequest.acceptEstimate(); // Move to ACCEPTED state

        // Act
        ServiceRequest updatedRequest = serviceRequestService.startService(requestId, technicianId);

        // Assert
        assertNotNull(updatedRequest);
        assertEquals("IN_PROGRESS", updatedRequest.getStateName());
        verify(serviceRequestRepository).findById(requestId);
        verify(technicianRepository).findById(technicianId);
        verify(serviceRequestRepository).save(updatedRequest);
    }

    @Test
    void testCompleteService() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        serviceRequest.provideEstimate(estimate); // Move to ESTIMATED state
        serviceRequest.acceptEstimate(); // Move to ACCEPTED state
        serviceRequest.startService(); // Move to IN_PROGRESS state

        // Record technician stats before completion
        int initialCompletedJobCount = technician.getCompletedJobCount();
        double initialTotalEarnings = technician.getTotalEarnings();

        // Act
        ServiceRequest updatedRequest = serviceRequestService.completeService(requestId, technicianId);

        // Assert
        assertNotNull(updatedRequest);
        assertEquals("COMPLETED", updatedRequest.getStateName());
        assertEquals(initialCompletedJobCount + 1, technician.getCompletedJobCount());
        assertEquals(initialTotalEarnings + estimate.getCost(), technician.getTotalEarnings(), 0.001);
        verify(serviceRequestRepository).findById(requestId);
        verify(technicianRepository).findById(technicianId);
        verify(serviceRequestRepository).save(updatedRequest);
    }

    @Test
    void testCreateReport() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        serviceRequest.provideEstimate(estimate); // Move to ESTIMATED state
        serviceRequest.acceptEstimate(); // Move to ACCEPTED state
        serviceRequest.startService(); // Move to IN_PROGRESS state
        serviceRequest.completeService(); // Move to COMPLETED state

        Report report = new Report();
        report.setRepairDetails("Replaced screen with a new one");
        report.setRepairSummary("Fixed the cracked screen");
        report.setCompletionDateTime(LocalDateTime.now());

        // Act
        ServiceRequest updatedRequest = serviceRequestService.createReport(requestId, report, technicianId);

        // Assert
        assertNotNull(updatedRequest);
        assertEquals(report, updatedRequest.getReport());
        assertEquals("COMPLETED", updatedRequest.getStateName()); // State remains COMPLETED
        verify(serviceRequestRepository).findById(requestId);
        verify(technicianRepository).findById(technicianId);
        verify(serviceRequestRepository).save(updatedRequest);
    }

    @Test
    void testProvideEstimate_InvalidState() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));

        // Move the request through states to COMPLETED
        serviceRequest.provideEstimate(estimate);
        serviceRequest.acceptEstimate();
        serviceRequest.startService();
        serviceRequest.completeService();

        // Create a new estimate
        RepairEstimate newEstimate = new RepairEstimate();
        newEstimate.setCost(150.0);
        newEstimate.setCompletionDate(LocalDate.now().plusDays(5));

        // Modify the service implementation to check state first
        when(serviceRequestRepository.findById(requestId)).thenReturn(Optional.of(serviceRequest));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            serviceRequestService.provideEstimate(requestId, newEstimate, technicianId);
        });
    }

    @Test
    void testAcceptEstimate_InvalidState() {
        // Arrange - service request in PENDING state (no estimate provided)
        // Make sure we're using the actual customer
        when(serviceRequestRepository.findById(requestId)).thenReturn(Optional.of(serviceRequest));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            serviceRequestService.acceptEstimate(requestId, customerId);
        });
    }
}