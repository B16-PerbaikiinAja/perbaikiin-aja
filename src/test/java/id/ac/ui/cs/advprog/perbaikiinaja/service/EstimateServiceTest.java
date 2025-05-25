package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
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
        lenient().when(customer.getId()).thenReturn(customerId);

        // Set up estimate
        estimate = mock(RepairEstimate.class);
        lenient().when(estimate.getId()).thenReturn(estimateId);

        // Set up service request
        serviceRequest = mock(ServiceRequest.class);
        lenient().when(serviceRequest.getId()).thenReturn(serviceRequestId);
        lenient().when(serviceRequest.getEstimate()).thenReturn(estimate);
        lenient().when(serviceRequest.getCustomer()).thenReturn(customer);
    }

    @Test
    void findById_WithValidId_ShouldReturnEstimate() {
        // Arrange
        when(serviceRequestRepository.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));

        // Act
        Optional<RepairEstimate> result = estimateService.findById(serviceRequestId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(estimate, result.get());
    }

    @Test
    void acceptEstimate_WithValidParams_ShouldAcceptEstimate() {
        // Arrange
        when(serviceRequestRepository.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequestRepository.save(serviceRequest)).thenReturn(serviceRequest);

        // Act
        ServiceRequest result = estimateService.acceptEstimate(serviceRequestId, customerId, "Great estimate!");

        // Assert
        assertNotNull(result);
        assertEquals(serviceRequestId, result.getId());
        verify(serviceRequest).acceptEstimate();
        verify(serviceRequestRepository).save(serviceRequest);
    }

    @Test
    void rejectEstimate_WithValidParams_ShouldRejectAndDeleteServiceRequest() {
        // Arrange
        when(serviceRequestRepository.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));
        doNothing().when(serviceRequestRepository).delete(serviceRequest);

        // Act
        UUID result = estimateService.rejectEstimate(serviceRequestId, customerId, "Too expensive");

        // Assert
        assertEquals(serviceRequestId, result);
        verify(serviceRequest).rejectEstimate();
        verify(serviceRequestRepository).delete(serviceRequest);
    }

    @Test
    void findById_WithInvalidId_ShouldReturnEmpty() {
        // Arrange
        when(serviceRequestRepository.findById(serviceRequestId)).thenReturn(Optional.empty());

        // Act
        Optional<RepairEstimate> result = estimateService.findById(serviceRequestId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void acceptEstimate_WithInvalidServiceRequestId_ShouldThrowException() {
        // Arrange
        UUID invalidServiceRequestId = UUID.randomUUID();
        when(serviceRequestRepository.findById(invalidServiceRequestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            estimateService.acceptEstimate(invalidServiceRequestId, customerId, "Great estimate!");
        });
    }

    @Test
    void acceptEstimate_WithInvalidCustomerId_ShouldThrowException() {
        // Arrange
        UUID invalidCustomerId = UUID.randomUUID();
        when(serviceRequestRepository.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            estimateService.acceptEstimate(serviceRequestId, invalidCustomerId, "Great estimate!");
        });
    }

    @Test
    void acceptEstimate_WithAlreadyAcceptedEstimate_ShouldThrowException() {
        // Arrange
        when(serviceRequestRepository.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));
        when(serviceRequest.getStateType()).thenReturn(ServiceRequestStateType.ACCEPTED);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            estimateService.acceptEstimate(serviceRequestId, customerId, "Great estimate!");
        });
    }

    @Test
    void rejectEstimate_WithInvalidServiceRequestId_ShouldThrowException() {
        // Arrange
        UUID invalidServiceRequestId = UUID.randomUUID();
        when(serviceRequestRepository.findById(invalidServiceRequestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            estimateService.rejectEstimate(invalidServiceRequestId, customerId, "Too expensive");
        });
    }

    @Test
    void rejectEstimate_WithInvalidCustomerId_ShouldThrowException() {
        // Arrange
        UUID invalidCustomerId = UUID.randomUUID();
        when(serviceRequestRepository.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            estimateService.rejectEstimate(serviceRequestId, invalidCustomerId, "Too expensive");
        });
    }

    @Test
    void findById_WithServiceRequestWithoutEstimate_ShouldReturnEmpty() {
        // Arrange
        when(serviceRequest.getEstimate()).thenReturn(null);
        when(serviceRequestRepository.findById(serviceRequestId)).thenReturn(Optional.of(serviceRequest));

        // Act
        Optional<RepairEstimate> result = estimateService.findById(serviceRequestId);

        // Assert
        assertFalse(result.isPresent());
    }
}