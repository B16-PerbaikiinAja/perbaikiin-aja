package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.config.TestSecurityConfig;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Item;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ReportService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(AdminReportController.class)
@Import(TestSecurityConfig.class)
public class AdminReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

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

        // Create customer mock
        Customer customer = mock(Customer.class);
        UUID customerId = UUID.randomUUID();
        when(customer.getId()).thenReturn(customerId);
        when(customer.getFullName()).thenReturn("Test Customer");

        // Create item mocks
        Item item = mock(Item.class);
        when(item.getName()).thenReturn("Test Item");
        when(item.getCondition()).thenReturn("Good condition");
        when(item.getIssueDescription()).thenReturn("Test issue description");

        // Create technician mock
        Technician technician = mock(Technician.class);
        when(technician.getId()).thenReturn(technicianId);
        when(technician.getFullName()).thenReturn("Test Technician");

        // Create report mocks
        Report report1 = mock(Report.class);
        Report report2 = mock(Report.class);
        when(report1.getId()).thenReturn(reportId);
        when(report2.getId()).thenReturn(UUID.randomUUID());
        when(report1.getRepairDetails()).thenReturn("Repair details 1");
        when(report2.getRepairDetails()).thenReturn("Repair details 2");
        when(report1.getRepairSummary()).thenReturn("Repair summary 1");
        when(report2.getRepairSummary()).thenReturn("Repair summary 2");
        when(report1.getCompletionDateTime()).thenReturn(LocalDateTime.now());
        when(report2.getCompletionDateTime()).thenReturn(LocalDateTime.now());
        when(report1.getCreatedDateTime()).thenReturn(LocalDateTime.now());
        when(report2.getCreatedDateTime()).thenReturn(LocalDateTime.now());

        // Create service request mocks
        ServiceRequest serviceRequest1 = mock(ServiceRequest.class);
        ServiceRequest serviceRequest2 = mock(ServiceRequest.class);
        when(serviceRequest1.getId()).thenReturn(UUID.randomUUID());
        when(serviceRequest2.getId()).thenReturn(UUID.randomUUID());
        when(serviceRequest1.getCustomer()).thenReturn(customer);
        when(serviceRequest2.getCustomer()).thenReturn(customer);
        when(serviceRequest1.getItem()).thenReturn(item);
        when(serviceRequest2.getItem()).thenReturn(item);
        when(serviceRequest1.getTechnician()).thenReturn(technician);
        when(serviceRequest2.getTechnician()).thenReturn(technician);
        when(serviceRequest1.getStateName()).thenReturn("COMPLETED");
        when(serviceRequest2.getStateName()).thenReturn("COMPLETED");

        // Connect reports to service requests
        when(report1.getServiceRequest()).thenReturn(serviceRequest1);
        when(report2.getServiceRequest()).thenReturn(serviceRequest2);

        reports = Arrays.asList(report1, report2);

        when(reportService.getAllReports()).thenReturn(reports);
        when(reportService.getReportsByTechnician(technicianId)).thenReturn(reports);
    }

    @Test
    void getReports_ShouldReturnAllReports() throws Exception {
        mockMvc.perform(get("/admin/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(admin)))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reports").isArray());

        verify(reportService).getReportsByDateRange(startDate, endDate);
    }

    @Test
    void getReports_ByTechnicianId_ShouldFilterByTechnician() throws Exception {
        when(reportService.getReportsByTechnician(technicianId)).thenReturn(reports);

        mockMvc.perform(get("/admin/report")
                        .param("technicianId", technicianId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(admin)))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reports").isArray())
                .andExpect(jsonPath("$.reports.length()").value(1));

        verify(reportService).getReportById(reportId);
    }

    @Test
    void getReports_WithInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/admin/report")
                        .param("date_start", "invalid-date")
                        .param("date_end", LocalDate.now().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(admin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(4000));
    }

    @Test
    void getReports_WithNonAdminUser_ShouldReturnForbidden() throws Exception {
        Technician technician = mock(Technician.class);
        when(technician.getRole()).thenReturn("TECHNICIAN");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(technician, null)
        );

        mockMvc.perform(get("/admin/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(technician)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(4030));
    }
}
