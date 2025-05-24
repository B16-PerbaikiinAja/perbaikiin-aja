package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ReportRepository;
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
    private final ReportRepository reportRepository;

    @Autowired
    public ReportServiceImpl(ServiceRequestRepository serviceRequestRepository, ReportRepository reportRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public List<Report> getAllReports() {
        return (List<Report>) reportRepository.findAll();
    }

    @Override
    public Report getReportById(UUID reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with ID: " + reportId));
    }

    @Override
    public List<Report> getReportsByTechnician(UUID technicianId) {
        return reportRepository.findByServiceRequest_TechnicianId(technicianId);
    }

    @Override
    public List<Report> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return reportRepository.findByCompletionDateTimeBetween(startDate, endDate);
    }
}