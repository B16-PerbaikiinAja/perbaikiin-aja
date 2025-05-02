package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstimateServiceTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @InjectMocks
    private EstimateServiceImpl estimateService;

    private UUID estimateId;
    private UUID customerId;
    private UUID serviceRequestId;
    private RepairEstimate estimate;
    private ServiceRequest serviceRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Initialize test data
        estimateId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        serviceRequestId = UUID.randomUUID();

        // Set up customer
        customer = mock(Customer.class);
        when(customer.getId()).thenReturn(customerId);

        // Set up estimate
        estimate = mock(RepairEstimate.class);
        when(estimate.getId()).thenReturn(estimateId);

        // Set up service request
        serviceRequest = mock(ServiceRequest.class);
        when(serviceRequest.getId()).thenReturn(serviceRequestId);
        when(serviceRequest.getEstimate()).thenReturn(estimate);
        when(serviceRequest.getCustomer()).thenReturn(customer);
    }

    @Test
    void findById_WithValidId_ShouldReturnEstimate() {
        // Arrange
        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(serviceRequest));

        // Act
        Optional<RepairEstimate> result = estimateService.findById(estimateId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(estimateId, result.get().getId());
    }

    @Test
    void getServiceRequest_WithValidEstimate_ShouldReturnServiceRequest() {
        // Arrange
        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(serviceRequest));

        // Act
        ServiceRequest result = estimateService.getServiceRequest(estimate);

        // Assert
        assertNotNull(result);
        assertEquals(serviceRequestId, result.getId());
    }

    @Test
    void acceptEstimate_WithValidParams_ShouldAcceptEstimate() {
        // Arrange
        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(serviceRequest));
        when(serviceRequestRepository.save(serviceRequest)).thenReturn(serviceRequest);

        // Act
        ServiceRequest result = estimateService.acceptEstimate(estimateId, customerId, "Great estimate!");

        // Assert
        assertNotNull(result);
        assertEquals(serviceRequestId, result.getId());
        verify(serviceRequest).acceptEstimate();
        verify(serviceRequestRepository).save(serviceRequest);
    }

    @Test
    void rejectEstimate_WithValidParams_ShouldRejectAndDeleteServiceRequest() {
        // Arrange
        when(serviceRequestRepository.findAll()).thenReturn(Arrays.asList(serviceRequest));
        doNothing().when(serviceRequestRepository).delete(serviceRequest);

        // Act
        UUID result = estimateService.rejectEstimate(estimateId, customerId, "Too expensive");

        // Assert
        assertEquals(serviceRequestId, result);
        verify(serviceRequest).rejectEstimate();
        verify(serviceRequestRepository).delete(serviceRequest);
    }
}
