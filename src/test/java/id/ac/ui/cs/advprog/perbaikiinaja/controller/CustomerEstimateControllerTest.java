package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.service.EstimateService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(CustomerEstimateController.class)
public class CustomerEstimateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EstimateService estimateService;

    private Customer customer;
    private UUID customerId;
    private UUID estimateId;
    private RepairEstimate estimate;
    private ServiceRequest serviceRequest;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        estimateId = UUID.randomUUID();

        customer = mock(Customer.class);
        when(customer.getId()).thenReturn(customerId);
        when(customer.getRole()).thenReturn("CUSTOMER");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customer, null)
        );

        estimate = mock(RepairEstimate.class);
        when(estimate.getId()).thenReturn(estimateId);

        serviceRequest = mock(ServiceRequest.class);
        UUID serviceRequestId = UUID.randomUUID();
        when(serviceRequest.getId()).thenReturn(serviceRequestId);
        when(serviceRequest.getCustomer()).thenReturn(customer);
        when(serviceRequest.getEstimate()).thenReturn(estimate);

        when(estimateService.findById(estimateId)).thenReturn(Optional.of(estimate));
        when(estimateService.getServiceRequest(estimate)).thenReturn(serviceRequest);
    }
}
