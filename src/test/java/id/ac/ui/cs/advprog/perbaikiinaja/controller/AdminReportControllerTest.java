package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminReportController.class)
public class AdminReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    private Admin admin;
    private UUID technicianId;
    private UUID reportId;
    private List<Report> reports;

    @BeforeEach
    void setUp() {
        admin = mock(Admin.class);
        when(admin.getRole()).thenReturn("ADMIN");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(admin, null)
        );

        technicianId = UUID.randomUUID();
        reportId = UUID.randomUUID();

        Report report1 = mock(Report.class);
        Report report2 = mock(Report.class);

        ServiceRequest serviceRequest1 = mock(ServiceRequest.class);
        ServiceRequest serviceRequest2 = mock(ServiceRequest.class);

        Technician technician = mock(Technician.class);
        when(technician.getId()).thenReturn(technicianId);

        when(report1.getId()).thenReturn(reportId);
        when(report2.getId()).thenReturn(UUID.randomUUID());

        when(report1.getServiceRequest()).thenReturn(serviceRequest1);
        when(report2.getServiceRequest()).thenReturn(serviceRequest2);

        when(serviceRequest1.getTechnician()).thenReturn(technician);
        when(serviceRequest2.getTechnician()).thenReturn(technician);

        reports = Arrays.asList(report1, report2);

        when(reportService.getAllReports()).thenReturn(reports);
    }

    @Test
    void getReports_ShouldReturnAllReports() throws Exception {
        mockMvc.perform(get("/admin/report")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reports").isArray())
                .andExpect(jsonPath("$.reports.length()").value(2));

        verify(reportService).getAllReports();
    }

    @Test
    void getReports_WithDateRange_ShouldFilterByDateRange() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        when(reportService.getReportsByDateRange(startDate, endDate)).thenReturn(reports);

        mockMvc.perform(get("/admin/report")
                        .param("date_start", startDate.toString())
                        .param("date_end", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reports").isArray());

        verify(reportService).getReportsByDateRange(startDate, endDate);
    }

    @Test
    void getReports_ByTechnicianId_ShouldFilterByTechnician() throws Exception {
        when(reportService.getReportsByTechnician(technicianId)).thenReturn(reports);

        mockMvc.perform(get("/admin/report")
                        .param("technicianId", technicianId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reports").isArray());

        verify(reportService).getReportsByTechnician(technicianId);
    }

    @Test
    void getReports_ByReportId_ShouldReturnSingleReport() throws Exception {
        Report report = reports.get(0);
        when(reportService.getReportById(reportId)).thenReturn(report);

        mockMvc.perform(get("/admin/report")
                        .param("reportId", reportId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reports").isArray())
                .andExpect(jsonPath("$.reports.length()").value(1));

        verify(reportService).getReportById(reportId);
    }
}
