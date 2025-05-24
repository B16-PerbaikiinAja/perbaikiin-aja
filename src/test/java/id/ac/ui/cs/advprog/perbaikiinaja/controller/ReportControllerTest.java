package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.builder.ReportBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Item;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ReportService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    @Mock
    private ServiceRequestService serviceRequestService;

    @Mock
    private ReportService reportService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReportController controller;

    private UUID technicianId;
    private UUID customerId;
    private UUID serviceRequestId;
    private UUID reportId;
    private Technician technician;
    private Customer customer;
    private Admin admin;
    private ServiceRequest serviceRequest;
    private Report report;
    private Item item;
    private List<Report> reports;

    @BeforeEach
    void setUp() {
        // Initialize test data
        technicianId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        serviceRequestId = UUID.randomUUID();
        reportId = UUID.randomUUID();

        // Mock technician
        technician = mock(Technician.class);
        lenient().when(technician.getId()).thenReturn(technicianId);
        lenient().when(technician.getRole()).thenReturn("TECHNICIAN");
        lenient().when(technician.getFullName()).thenReturn("John Tech");

        // Mock customer
        customer = mock(Customer.class);
        lenient().when(customer.getId()).thenReturn(customerId);
        lenient().when(customer.getRole()).thenReturn("CUSTOMER");
        lenient().when(customer.getFullName()).thenReturn("Jane Customer");

        // Mock admin
        admin = mock(Admin.class);
        lenient().when(admin.getRole()).thenReturn("ADMIN");

        // Mock item
        item = mock(Item.class);
        lenient().when(item.getName()).thenReturn("Smartphone");
        lenient().when(item.getCondition()).thenReturn("Cracked screen");
        lenient().when(item.getIssueDescription()).thenReturn("Screen unresponsive");

        // Mock report
        report = mock(Report.class);
        lenient().when(report.getId()).thenReturn(reportId);
        lenient().when(report.getRepairDetails()).thenReturn("Replaced the broken screen");
        lenient().when(report.getRepairSummary()).thenReturn("Fixed the cracked screen");
        lenient().when(report.getCompletionDateTime()).thenReturn(LocalDateTime.now());
        lenient().when(report.getCreatedDateTime()).thenReturn(LocalDateTime.now());

        // Mock service request
        serviceRequest = mock(ServiceRequest.class);
        lenient().when(serviceRequest.getId()).thenReturn(serviceRequestId);
        lenient().when(serviceRequest.getTechnician()).thenReturn(technician);
        lenient().when(serviceRequest.getCustomer()).thenReturn(customer);
        lenient().when(serviceRequest.getItem()).thenReturn(item);
        lenient().when(serviceRequest.getStateType()).thenReturn(ServiceRequestStateType.COMPLETED);
        lenient().when(serviceRequest.getReport()).thenReturn(report);

        // Create list of reports
        reports = Arrays.asList(report);
    }

    @Test
    void createReport_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.createReport(eq(serviceRequestId), any(Report.class), eq(technicianId)))
                .thenReturn(serviceRequest);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", serviceRequestId.toString());
        requestBody.put("repairDetails", "Replaced the broken screen");
        requestBody.put("resolutionSummary", "Fixed the cracked screen");
        requestBody.put("completionDate", LocalDateTime.now().toString());

        // Act
        ResponseEntity<?> response = controller.createReport(requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        Map<String, Object> reportResponse = (Map<String, Object>) responseBody.get("report");
        assertNotNull(reportResponse);
        assertEquals(reportId, reportResponse.get("id"));
        assertEquals(serviceRequestId.toString(), reportResponse.get("serviceRequestId"));

        verify(serviceRequestService).createReport(eq(serviceRequestId), any(Report.class), eq(technicianId));
    }

    @Test
    void createReport_MissingRequiredFields_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", serviceRequestId.toString());
        // Missing repairDetails, resolutionSummary, and completionDate

        // Act
        ResponseEntity<?> response = controller.createReport(requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(serviceRequestService, never()).createReport(any(), any(), any());
    }

    @Test
    void createReport_InvalidServiceRequestId_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", "not-a-uuid");
        requestBody.put("repairDetails", "Replaced the broken screen");
        requestBody.put("resolutionSummary", "Fixed the cracked screen");
        requestBody.put("completionDate", LocalDateTime.now().toString());

        // Act
        ResponseEntity<?> response = controller.createReport(requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4000, responseBody.get("errorCode"));

        verify(serviceRequestService, never()).createReport(any(), any(), any());
    }

    @Test
    void createReport_InvalidCompletionDate_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", serviceRequestId.toString());
        requestBody.put("repairDetails", "Replaced the broken screen");
        requestBody.put("resolutionSummary", "Fixed the cracked screen");
        requestBody.put("completionDate", "not-a-date");

        // Act
        ResponseEntity<?> response = controller.createReport(requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4000, responseBody.get("errorCode"));

        verify(serviceRequestService, never()).createReport(any(), any(), any());
    }

    @Test
    void createReport_InvalidReportData_ReturnsBadRequest() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);

        // This is a special mock setup to make the builder's build() method throw an exception
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", serviceRequestId.toString());
        requestBody.put("repairDetails", ""); // Empty repair details should cause validation to fail
        requestBody.put("resolutionSummary", "Fixed the cracked screen");
        requestBody.put("completionDate", LocalDateTime.now().toString());

        // Act
        ResponseEntity<?> response = controller.createReport(requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(serviceRequestService, never()).createReport(any(), any(), any());
    }

    @Test
    void createReport_ServiceRequestNotFound_ReturnsNotFound() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.createReport(eq(serviceRequestId), any(Report.class), eq(technicianId)))
                .thenThrow(new IllegalArgumentException("Service request not found"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", serviceRequestId.toString());
        requestBody.put("repairDetails", "Replaced the broken screen");
        requestBody.put("resolutionSummary", "Fixed the cracked screen");
        requestBody.put("completionDate", LocalDateTime.now().toString());

        // Act
        ResponseEntity<?> response = controller.createReport(requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4040, responseBody.get("errorCode"));
    }

    @Test
    void createReport_InvalidState_ReturnsConflict() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(technician);
        when(serviceRequestService.createReport(eq(serviceRequestId), any(Report.class), eq(technicianId)))
                .thenThrow(new IllegalStateException("Cannot create report in current state"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("serviceRequestId", serviceRequestId.toString());
        requestBody.put("repairDetails", "Replaced the broken screen");
        requestBody.put("resolutionSummary", "Fixed the cracked screen");
        requestBody.put("completionDate", LocalDateTime.now().toString());

        // Act
        ResponseEntity<?> response = controller.createReport(requestBody, authentication);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4090, responseBody.get("errorCode"));
    }

    @Test
    void getReports_AllReports_Success() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(admin);
        when(reportService.getAllReports()).thenReturn(reports);
        when(reportService.getServiceRequestByReportId(reportId)).thenReturn(serviceRequest);

        // Act
        ResponseEntity<?> response = controller.getReports(null, null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> reportsList = (List<Map<String, Object>>) responseBody.get("reports");
        assertNotNull(reportsList);
        assertEquals(1, reportsList.size());

        verify(reportService).getAllReports();
        verify(reportService).getServiceRequestByReportId(reportId);
    }

    @Test
    void getReports_ByReportId_Success() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(admin);
        when(reportService.getReportById(reportId)).thenReturn(report);
        when(reportService.getServiceRequestByReportId(reportId)).thenReturn(serviceRequest);

        // Act
        ResponseEntity<?> response = controller.getReports(null, null, null, reportId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> reportsList = (List<Map<String, Object>>) responseBody.get("reports");
        assertNotNull(reportsList);
        assertEquals(1, reportsList.size());

        verify(reportService).getReportById(reportId);
        verify(reportService).getServiceRequestByReportId(reportId);
    }

    @Test
    void getReports_ByTechnicianId_Success() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(admin);
        when(reportService.getReportsByTechnician(technicianId)).thenReturn(reports);
        when(reportService.getServiceRequestByReportId(reportId)).thenReturn(serviceRequest);

        // Act
        ResponseEntity<?> response = controller.getReports(null, null, technicianId, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> reportsList = (List<Map<String, Object>>) responseBody.get("reports");
        assertNotNull(reportsList);
        assertEquals(1, reportsList.size());

        verify(reportService).getReportsByTechnician(technicianId);
        verify(reportService).getServiceRequestByReportId(reportId);
    }

    @Test
    void getReports_ByDateRange_Success() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(admin);
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(reportService.getReportsByDateRange(startDate, endDate)).thenReturn(reports);
        when(reportService.getServiceRequestByReportId(reportId)).thenReturn(serviceRequest);

        // Act
        ResponseEntity<?> response = controller.getReports(
                startDate.toString(),
                endDate.toString(),
                null,
                null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> reportsList = (List<Map<String, Object>>) responseBody.get("reports");
        assertNotNull(reportsList);
        assertEquals(1, reportsList.size());

        verify(reportService).getReportsByDateRange(startDate, endDate);
        verify(reportService).getServiceRequestByReportId(reportId);
    }

    @Test
    void getReports_InvalidDateFormat_ReturnsBadRequest() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(admin);

        // Act
        ResponseEntity<?> response = controller.getReports(
                "not-a-date",
                LocalDate.now().toString(),
                null,
                null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4000, responseBody.get("errorCode"));

        verifyNoInteractions(reportService);
    }

    @Test
    void getReports_ReportNotFound_ReturnsNotFound() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(admin);
        when(reportService.getReportById(reportId))
                .thenThrow(new IllegalArgumentException("Report not found"));

        // Act
        ResponseEntity<?> response = controller.getReports(null, null, null, reportId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4041, responseBody.get("errorCode"));
    }

    @Test
    void getReports_TechnicianNotFound_ReturnsNotFound() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(admin);
        when(reportService.getReportsByTechnician(technicianId))
                .thenThrow(new IllegalArgumentException("Technician not found"));

        // Act
        ResponseEntity<?> response = controller.getReports(null, null, technicianId, null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(4040, responseBody.get("errorCode"));
    }

    @Test
    void getTechnicianReports_Success() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(technician);
        when(reportService.getReportsByTechnician(technicianId)).thenReturn(reports);
        when(reportService.getServiceRequestByReportId(reportId)).thenReturn(serviceRequest);

        // Act
        ResponseEntity<?> response = controller.getTechnicianReports(technicianId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> reportsList = (List<Map<String, Object>>) responseBody.get("reports");
        assertNotNull(reportsList);
        assertEquals(1, reportsList.size());

        verify(reportService).getReportsByTechnician(technicianId);
        verify(reportService).getServiceRequestByReportId(reportId);
    }

    @Test
    void getTechnicianReports_Exception_ReturnsInternalServerError() {
        // Arrange
        lenient().when(authentication.getPrincipal()).thenReturn(technician);
        when(reportService.getReportsByTechnician(technicianId))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<?> response = controller.getTechnicianReports(technicianId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(5000, responseBody.get("errorCode"));
    }
}