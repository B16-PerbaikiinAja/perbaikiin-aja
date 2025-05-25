package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ReportRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public ReportServiceImpl(ServiceRequestRepository serviceRequestRepository, ReportRepository reportRepository, UserRepository userRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
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
        // Get all service requests for the technician
        List<ServiceRequest> serviceRequests = serviceRequestRepository.findByTechnicianId(technicianId);

        // Extract reports from service requests
        return serviceRequests.stream()
                .filter(sr -> sr.getReport() != null)
                .map(ServiceRequest::getReport)
                .toList();
    }

    @Override
    public List<Report> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        // Get all reports first
        List<Report> allReports = getAllReports();

        // Filter by date range
        return allReports.stream()
                .filter(report -> {
                    LocalDate completionDate = report.getCompletionDateTime().toLocalDate();
                    return !completionDate.isBefore(startDate) && !completionDate.isAfter(endDate);
                })
                .toList();
    }

    /**
     * Helper method to get all technicians from the user repository
     */
    private List<Technician> getAllTechnicians() {
        Iterable<User> allUsers = userRepository.findAll();

        return StreamSupport.stream(allUsers.spliterator(), false)
                .filter(Technician.class::isInstance)
                .map(Technician.class::cast)
                .toList();
    }

    /**
     * Helper method to find ServiceRequest by report ID
     * This is useful for methods that need to access ServiceRequest data
     */
    public ServiceRequest getServiceRequestByReportId(UUID reportId) {
        // Get all technicians and their service requests
        List<Technician> allTechnicians = getAllTechnicians();

        for (Technician technician : allTechnicians) {
            List<ServiceRequest> serviceRequests = serviceRequestRepository.findByTechnicianId(technician.getId());

            for (ServiceRequest sr : serviceRequests) {
                if (sr.getReport() != null && sr.getReport().getId().equals(reportId)) {
                    return sr;
                }
            }
        }

        throw new IllegalArgumentException("Service request not found for report ID: " + reportId);
    }


}