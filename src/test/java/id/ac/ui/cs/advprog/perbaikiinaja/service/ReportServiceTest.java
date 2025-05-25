package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ReportRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private UUID reportId;
    private UUID technicianId;
    private ServiceRequest serviceRequest1;
    private ServiceRequest serviceRequest2;
    private ServiceRequest serviceRequestWithoutReport;
    private Report report1;
    private Report report2;
    private Technician technician;
    private LocalDateTime baseDateTime;

    @BeforeEach
    void setUp() {
        // Initialize test data
        reportId = UUID.randomUUID();
        technicianId = UUID.randomUUID();
        baseDateTime = LocalDateTime.now();

        // Set up technician
        technician = mock(Technician.class);
        lenient().when(technician.getId()).thenReturn(technicianId);

        // Set up reports
        report1 = mock(Report.class);
        lenient().when(report1.getId()).thenReturn(reportId);
        lenient().when(report1.getCompletionDateTime()).thenReturn(baseDateTime.minusDays(5));

        report2 = mock(Report.class);
        lenient().when(report2.getId()).thenReturn(UUID.randomUUID());
        lenient().when(report2.getCompletionDateTime()).thenReturn(baseDateTime.minusDays(10));

        // Set up service requests
        serviceRequest1 = mock(ServiceRequest.class);
        lenient().when(serviceRequest1.getReport()).thenReturn(report1);
        lenient().when(serviceRequest1.getTechnician()).thenReturn(technician);

        serviceRequest2 = mock(ServiceRequest.class);
        lenient().when(serviceRequest2.getReport()).thenReturn(report2);
        lenient().when(serviceRequest2.getTechnician()).thenReturn(technician);

        serviceRequestWithoutReport = mock(ServiceRequest.class);
        lenient().when(serviceRequestWithoutReport.getReport()).thenReturn(null);

        // Mock userRepository to return the technician as a User
        lenient().when(userRepository.findAll()).thenReturn(Arrays.asList((User) technician));
    }

    @Test
    void getAllReports_ShouldReturnAllReportsFromReportRepository() {
        // Arrange
        when(reportRepository.findAll()).thenReturn(Arrays.asList(report1, report2));

        // Act
        List<Report> reports = reportService.getAllReports();

        // Assert
        assertEquals(2, reports.size());
        assertTrue(reports.contains(report1));
        assertTrue(reports.contains(report2));
        verify(reportRepository).findAll();
    }

    @Test
    void getReportById_WithValidId_ShouldReturnReport() {
        // Arrange
        when(reportRepository.findById(reportId)).thenReturn(java.util.Optional.of(report1));

        // Act
        Report result = reportService.getReportById(reportId);

        // Assert
        assertNotNull(result);
        assertEquals(reportId, result.getId());
        verify(reportRepository).findById(reportId);
    }

    @Test
    void getReportById_WithInvalidId_ShouldThrowException() {
        // Arrange
        UUID invalidId = UUID.randomUUID();
        when(reportRepository.findById(invalidId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reportService.getReportById(invalidId);
        });
        verify(reportRepository).findById(invalidId);
    }

    @Test
    void getReportsByTechnician_ShouldReturnTechnicianReports() {
        // Arrange
        when(serviceRequestRepository.findByTechnicianId(technicianId))
                .thenReturn(Arrays.asList(serviceRequest1, serviceRequest2));

        // Act
        List<Report> reports = reportService.getReportsByTechnician(technicianId);

        // Assert
        assertEquals(2, reports.size());
        assertTrue(reports.contains(report1));
        assertTrue(reports.contains(report2));
        verify(serviceRequestRepository).findByTechnicianId(technicianId);
    }

    @Test
    void getReportsByDateRange_ShouldFilterByDateRange() {
        // Arrange
        when(reportRepository.findAll()).thenReturn(Arrays.asList(report1, report2));

        LocalDate startDate = baseDateTime.minusDays(7).toLocalDate();
        LocalDate endDate = baseDateTime.toLocalDate();

        // Act
        List<Report> reports = reportService.getReportsByDateRange(startDate, endDate);

        // Assert
        assertEquals(1, reports.size());
        assertTrue(reports.contains(report1)); // Only report1 falls within the date range
        verify(reportRepository).findAll();
    }

    @Test
    void getReportsByDateRange_WithEmptyDateRange_ShouldReturnEmptyList() {
        // Arrange
        when(reportRepository.findAll()).thenReturn(Arrays.asList(report1, report2));

        LocalDate futureStartDate = baseDateTime.plusDays(1).toLocalDate();
        LocalDate futureEndDate = baseDateTime.plusDays(7).toLocalDate();

        // Act
        List<Report> reports = reportService.getReportsByDateRange(futureStartDate, futureEndDate);

        // Assert
        assertTrue(reports.isEmpty());
        verify(reportRepository).findAll();
    }

    @Test
    void getServiceRequestByReportId_WithValidId_ShouldReturnServiceRequest() {
        // Arrange
        when(serviceRequestRepository.findByTechnicianId(technicianId))
                .thenReturn(Arrays.asList(serviceRequest1, serviceRequest2));

        // Act
        ServiceRequest result = reportService.getServiceRequestByReportId(reportId);

        // Assert
        assertNotNull(result);
        assertEquals(serviceRequest1, result);
        verify(userRepository).findAll();
        verify(serviceRequestRepository).findByTechnicianId(technicianId);
    }

    @Test
    void getServiceRequestByReportId_WithInvalidId_ShouldThrowException() {
        // Arrange
        UUID invalidReportId = UUID.randomUUID();
        when(serviceRequestRepository.findByTechnicianId(technicianId))
                .thenReturn(Arrays.asList(serviceRequest1, serviceRequest2));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reportService.getServiceRequestByReportId(invalidReportId);
        });
        verify(userRepository).findAll();
        verify(serviceRequestRepository).findByTechnicianId(technicianId);
    }

    @Test
    void getReportsByTechnician_WithNoReports_ShouldReturnEmptyList() {
        // Arrange
        ServiceRequest mockedServiceRequestWithoutReport = mock(ServiceRequest.class);
        when(serviceRequestWithoutReport.getReport()).thenReturn(null);

        when(serviceRequestRepository.findByTechnicianId(technicianId))
                .thenReturn(Arrays.asList(serviceRequestWithoutReport));

        // Act
        List<Report> reports = reportService.getReportsByTechnician(technicianId);

        // Assert
        assertTrue(reports.isEmpty());
        verify(serviceRequestRepository).findByTechnicianId(technicianId);
    }

    @Test
    void getAllReports_WithNoReports_ShouldReturnEmptyList() {
        // Arrange
        when(reportRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Report> reports = reportService.getAllReports();

        // Assert
        assertTrue(reports.isEmpty());
        verify(reportRepository).findAll();
    }
}