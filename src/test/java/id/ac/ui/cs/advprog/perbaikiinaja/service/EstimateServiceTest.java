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

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}
