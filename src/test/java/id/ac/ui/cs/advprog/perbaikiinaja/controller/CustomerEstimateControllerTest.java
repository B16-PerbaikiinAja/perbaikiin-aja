package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.service.EstimateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void acceptEstimate_ShouldReturnSuccess() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "ACCEPT");
        requestBody.put("feedback", "Great estimate!");

        when(estimateService.acceptEstimate(estimateId, customerId, "Great estimate!"))
                .thenReturn(serviceRequest);
        when(serviceRequest.getStateName()).thenReturn("CONFIRMED");

        mockMvc.perform(put("/customer/estimates/{estimateId}/response", estimateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estimate.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.estimate.feedback").value("Great estimate!"))
                .andExpect(jsonPath("$.serviceRequest.status").value("CONFIRMED"));

        verify(estimateService).acceptEstimate(estimateId, customerId, "Great estimate!");
    }

    @Test
    void rejectEstimate_ShouldReturnSuccess() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "REJECT");
        requestBody.put("feedback", "Too expensive");

        UUID serviceRequestId = serviceRequest.getId();
        when(estimateService.rejectEstimate(estimateId, customerId, "Too expensive"))
                .thenReturn(serviceRequestId);

        mockMvc.perform(put("/customer/estimates/{estimateId}/response", estimateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Estimate rejected and service request deleted successfully"))
                .andExpect(jsonPath("$.estimateId").value(estimateId.toString()))
                .andExpect(jsonPath("$.serviceRequestId").value(serviceRequestId.toString()));

        verify(estimateService).rejectEstimate(estimateId, customerId, "Too expensive");
    }

    @Test
    void respondToEstimate_WithInvalidAction_ShouldReturnBadRequest() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "INVALID_ACTION");

        mockMvc.perform(put("/customer/estimates/{estimateId}/response", estimateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(4000));
    }

    @Test
    void respondToEstimate_WithEstimateNotFound_ShouldReturnNotFound() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "ACCEPT");

        UUID nonExistentId = UUID.randomUUID();
        when(estimateService.findById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/customer/estimates/{estimateId}/response", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(4040));
    }

    @Test
    void respondToEstimate_WithUnauthorizedCustomer_ShouldReturnForbidden() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "ACCEPT");

        Customer differentCustomer = mock(Customer.class);
        UUID differentCustomerId = UUID.randomUUID();
        when(differentCustomer.getId()).thenReturn(differentCustomerId);

        ServiceRequest serviceRequestWithDifferentCustomer = mock(ServiceRequest.class);
        when(serviceRequestWithDifferentCustomer.getCustomer()).thenReturn(differentCustomer);

        RepairEstimate estimateForDifferentCustomer = mock(RepairEstimate.class);
        UUID estimateForDifferentCustomerId = UUID.randomUUID();
        when(estimateForDifferentCustomer.getId()).thenReturn(estimateForDifferentCustomerId);

        when(estimateService.findById(estimateForDifferentCustomerId)).thenReturn(Optional.of(estimateForDifferentCustomer));
        when(estimateService.getServiceRequest(estimateForDifferentCustomer)).thenReturn(serviceRequestWithDifferentCustomer);

        mockMvc.perform(put("/customer/estimates/{estimateId}/response", estimateForDifferentCustomerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(4030));
    }
}
