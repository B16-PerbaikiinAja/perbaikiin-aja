package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
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
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TechnicianStatusController.class)
public class TechnicianStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceRequestService serviceRequestService;

    private Technician technician;
    private UUID technicianId;
    private UUID serviceRequestId;
    private ServiceRequest serviceRequest;

    @BeforeEach
    void setUp() {
        technicianId = UUID.randomUUID();
        serviceRequestId = UUID.randomUUID();

        technician = mock(Technician.class);
        when(technician.getId()).thenReturn(technicianId);
        when(technician.getRole()).thenReturn("TECHNICIAN");
        when(technician.getCompletedJobCount()).thenReturn(15);
        when(technician.getTotalEarnings()).thenReturn(3750000.0);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(technician, null)
        );

        serviceRequest = mock(ServiceRequest.class);
        when(serviceRequest.getId()).thenReturn(serviceRequestId);
        when(serviceRequest.getTechnician()).thenReturn(technician);
        when(serviceRequest.getStateName()).thenReturn("COMPLETED");

        when(serviceRequestService.findById(serviceRequestId)).thenReturn(java.util.Optional.of(serviceRequest));
        when(serviceRequestService.completeService(serviceRequestId, technicianId)).thenReturn(serviceRequest);
    }

    @Test
    void updateStatus_ToCompleted_ShouldReturnSuccess() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", "COMPLETED");
        requestBody.put("finalPrice", 250000);

        mockMvc.perform(put("/technician/service-requests/{serviceRequestId}/status", serviceRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceRequest.status").value("COMPLETED"))
                .andExpect(jsonPath("$.serviceRequest.finalPrice").value(250000))
                .andExpect(jsonPath("$.technician.completedJobsCount").value(15))
                .andExpect(jsonPath("$.technician.totalEarnings").value(3750000));

        verify(serviceRequestService).completeService(serviceRequestId, technicianId);
    }

    @Test
    void startService_ShouldReturnSuccess() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", "IN_PROGRESS");

        when(serviceRequestService.startService(serviceRequestId, technicianId)).thenReturn(serviceRequest);
        when(serviceRequest.getStateName()).thenReturn("IN_PROGRESS");

        mockMvc.perform(put("/technician/service-requests/{serviceRequestId}/status", serviceRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceRequest.status").value("IN_PROGRESS"));

        verify(serviceRequestService).startService(serviceRequestId, technicianId);
    }
}
