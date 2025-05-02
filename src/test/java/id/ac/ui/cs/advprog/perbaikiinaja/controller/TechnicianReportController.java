package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.perbaikiinaja.builder.ReportBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(TechnicianReportController.class)
public class TechnicianReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
}
