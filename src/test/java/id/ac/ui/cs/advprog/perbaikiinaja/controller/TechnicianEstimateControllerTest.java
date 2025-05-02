package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.config.TestSecurityConfig;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import id.ac.ui.cs.advprog.perbaikiinaja.services.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(TechnicianEstimateController.class)
@Import(TestSecurityConfig.class)
public class TechnicianEstimateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ServiceRequestService serviceRequestService;

    private Technician technician;
    private UUID technicianId;
    private UUID serviceRequestId;
    private ServiceRequest serviceRequest;
    private RepairEstimate estimate;

    @BeforeEach
    void setUp() {
        technicianId = UUID.randomUUID();
        serviceRequestId = UUID.randomUUID();

        technician = mock(Technician.class);
        when(technician.getId()).thenReturn(technicianId);
        when(technician.getRole()).thenReturn("TECHNICIAN");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(technician, null)
        );

        serviceRequest = mock(ServiceRequest.class);
        when(serviceRequest.getId()).thenReturn(serviceRequestId);

        estimate = new RepairEstimate();
        estimate.setCost(250000);
        estimate.setCompletionDate(LocalDate.now().plusDays(7));
        estimate.setNotes("Needs new parts");

        when(serviceRequestService.provideEstimate(eq(serviceRequestId), any(RepairEstimate.class), eq(technicianId)))
                .thenReturn(serviceRequest);
    }

    @Test
    void createEstimate_ShouldReturnCreated() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", 250000);
        requestBody.put("estimatedCompletionTime", LocalDate.now().plusDays(7).toString());
        requestBody.put("notes", "Needs new parts");

        // Set up the mock for getEstimate
        when(serviceRequest.getEstimate()).thenReturn(estimate);

        mockMvc.perform(post("/technician/service-requests/{serviceRequestId}/estimate", serviceRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(user(technician)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estimate.estimatedCost").value(250000))
                .andExpect(jsonPath("$.estimate.status").value("PENDING"));

        verify(serviceRequestService).provideEstimate(
                eq(serviceRequestId),
                any(RepairEstimate.class),
                eq(technicianId)
        );
    }

    @Test
    void createEstimate_WithNegativeCost_ShouldReturnBadRequest() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", -100);
        requestBody.put("estimatedCompletionTime", LocalDate.now().plusDays(7).toString());

        mockMvc.perform(post("/technician/service-requests/{serviceRequestId}/estimate", serviceRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(user(technician)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(4000));
    }

    @Test
    void createEstimate_WithPastDate_ShouldReturnBadRequest() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", 250000);
        requestBody.put("estimatedCompletionTime", LocalDate.now().minusDays(1).toString());

        mockMvc.perform(post("/technician/service-requests/{serviceRequestId}/estimate", serviceRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(user(technician)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(4001));
    }

    @Test
    void createEstimate_WithServiceRequestNotFound_ShouldReturnNotFound() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", 250000);
        requestBody.put("estimatedCompletionTime", LocalDate.now().plusDays(7).toString());

        when(serviceRequestService.provideEstimate(eq(serviceRequestId), any(RepairEstimate.class), eq(technicianId)))
                .thenThrow(new IllegalArgumentException("Service request not found"));

        mockMvc.perform(post("/technician/service-requests/{serviceRequestId}/estimate", serviceRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(user(technician)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(4040));
    }

    @Test
    void createEstimate_WithExistingEstimate_ShouldReturnConflict() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("estimatedCost", 250000);
        requestBody.put("estimatedCompletionTime", LocalDate.now().plusDays(7).toString());

        when(serviceRequestService.provideEstimate(eq(serviceRequestId), any(RepairEstimate.class), eq(technicianId)))
                .thenThrow(new IllegalStateException("Estimate already exists"));

        mockMvc.perform(post("/technician/service-requests/{serviceRequestId}/estimate", serviceRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(user(technician)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value(4090));
    }
}
