package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReportServiceImpl implements ReportService {

    private final ServiceRequestRepository serviceRequestRepository;

    @Autowired
    public ReportServiceImpl(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @Override
    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();

        Iterable<ServiceRequest> serviceRequests = serviceRequestRepository.findAll();
        StreamSupport.stream(serviceRequests.spliterator(), false)
                .filter(request -> request.getReport() != null)
                .forEach(request -> reports.add(request.getReport()));

        return reports;
    }

    @Override
    public Report getReportById(UUID reportId) {
        return getAllReports().stream()
                .filter(report -> report.getId().equals(reportId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Report not found with ID: " + reportId));
    }

    @Override
    public List<Report> getReportsByTechnician(UUID technicianId) {
        List<ServiceRequest> technicianRequests = serviceRequestRepository.findByTechnicianId(technicianId);

        return technicianRequests.stream()
                .filter(request -> request.getReport() != null)
                .map(ServiceRequest::getReport)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        return getAllReports().stream()
                .filter(report -> {
                    LocalDateTime completionDateTime = report.getCompletionDateTime();
                    return completionDateTime != null &&
                            completionDateTime.isAfter(startDateTime) &&
                            completionDateTime.isBefore(endDateTime);
                })
                .collect(Collectors.toList());
    }
}