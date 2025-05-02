package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.builder.ReportBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.config.TestSecurityConfig;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
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

import java.time.LocalDateTime;
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

@WebMvcTest(TechnicianReportController.class)
@Import(TestSecurityConfig.class)
public class TechnicianReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ServiceRequestService serviceRequestService;

    @MockBean
    private ReportBuilder reportBuilder;

    private Technician technician;
    private UUID technicianId;
    private UUID serviceRequestId;
    private ServiceRequest serviceRequest;
    private Report report;

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

        report = mock(Report.class);
        UUID reportId = UUID.randomUUID();
        when(report.getId()).thenReturn(reportId);
        when(report.getRepairDetails()).thenReturn("Replaced the broken parts");
        when(report.getRepairSummary()).thenReturn("Fixed the device");
        when(report.getCompletionDateTime()).thenReturn(LocalDateTime.now());
        when(report.getCreatedDateTime()).thenReturn(LocalDateTime.now());

        when(serviceRequestService.createReport(eq(serviceRequestId), any(Report.class), eq(technicianId)))
                .thenReturn(serviceRequest);
        when(serviceRequest.getReport()).thenReturn(report);
    }

    @Test
    void createReport_ShouldReturnCreated() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", serviceRequestId.toString());
        requestBody.put("repairDetails", "Replaced the broken parts");
        requestBody.put("resolutionSummary", "Fixed the device");
        requestBody.put("completionDate", LocalDateTime.now().toString());

        mockMvc.perform(post("/technician/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(user(technician)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.report.id").exists())
                .andExpect(jsonPath("$.report.serviceRequestId").value(serviceRequestId.toString()))
                .andExpect(jsonPath("$.report.repairDetails").value("Replaced the broken parts"))
                .andExpect(jsonPath("$.report.resolutionSummary").value("Fixed the device"));

        verify(serviceRequestService).createReport(
                eq(serviceRequestId),
                any(Report.class),
                eq(technicianId)
        );
    }

    @Test
    void createReport_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", serviceRequestId.toString());
        // Missing repairDetails, resolutionSummary, and completionDate

        mockMvc.perform(post("/technician/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(user(technician)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReport_WithServiceRequestNotFound_ShouldReturnNotFound() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", UUID.randomUUID().toString());
        requestBody.put("repairDetails", "Replaced the broken parts");
        requestBody.put("resolutionSummary", "Fixed the device");
        requestBody.put("completionDate", LocalDateTime.now().toString());

        when(serviceRequestService.createReport(any(), any(), eq(technicianId)))
                .thenThrow(new IllegalArgumentException("Service request not found"));

        mockMvc.perform(post("/technician/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(user(technician)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(4040));
    }

    @Test
    void createReport_WithIncompleteServiceRequest_ShouldReturnConflict() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", serviceRequestId.toString());
        requestBody.put("repairDetails", "Replaced the broken parts");
        requestBody.put("resolutionSummary", "Fixed the device");
        requestBody.put("completionDate", LocalDateTime.now().toString());

        when(serviceRequestService.createReport(eq(serviceRequestId), any(Report.class), eq(technicianId)))
                .thenThrow(new IllegalStateException("Service request is not completed yet"));

        mockMvc.perform(post("/technician/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .with(user(technician)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value(4090));
    }
}
