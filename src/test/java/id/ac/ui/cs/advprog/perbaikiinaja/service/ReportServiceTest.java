package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

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
        when(technician.getId()).thenReturn(technicianId);

        // Set up reports
        report1 = mock(Report.class);
        when(report1.getId()).thenReturn(reportId);
        when(report1.getCompletionDateTime()).thenReturn(baseDateTime.minusDays(5));

        report2 = mock(Report.class);
        when(report2.getId()).thenReturn(UUID.randomUUID());
        when(report2.getCompletionDateTime()).thenReturn(baseDateTime.minusDays(10));

        // Set up service requests
        serviceRequest1 = mock(ServiceRequest.class);
        when(serviceRequest1.getReport()).thenReturn(report1);
        when(serviceRequest1.getTechnician()).thenReturn(technician);
        when(report1.getServiceRequest()).thenReturn(serviceRequest1);

        serviceRequest2 = mock(ServiceRequest.class);
        when(serviceRequest2.getReport()).thenReturn(report2);
        when(serviceRequest2.getTechnician()).thenReturn(technician);
        when(report2.getServiceRequest()).thenReturn(serviceRequest2);

        serviceRequestWithoutReport = mock(ServiceRequest.class);
        when(serviceRequestWithoutReport.getReport()).thenReturn(null);
    }
}
