package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.builder.ReportBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.builder.RepairReportBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ReportService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ServiceRequestService serviceRequestService;
    private final ReportService reportService;

    @Autowired
    public ReportController(ServiceRequestService serviceRequestService, ReportService reportService) {
        this.serviceRequestService = serviceRequestService;
        this.reportService = reportService;
    }

    /**
     * Create a report as a technician
     */
    @PostMapping("/technician")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<?> createReport(
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        UUID technicianId = currentUser.getId();

        // Validate required fields
        if (!requestBody.containsKey("serviceRequestId") ||
                !requestBody.containsKey("repairDetails") ||
                !requestBody.containsKey("resolutionSummary") ||
                !requestBody.containsKey("completionDate")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UUID serviceRequestId;
        try {
            serviceRequestId = UUID.fromString((String) requestBody.get("serviceRequestId"));
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4000);
            response.put("message", "Invalid service request ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String repairDetails = (String) requestBody.get("repairDetails");
        String resolutionSummary = (String) requestBody.get("resolutionSummary");

        // Parse completion date
        LocalDateTime completionDateTime;
        try {
            String completionDateStr = (String) requestBody.get("completionDate");
            // Try to parse as LocalDateTime
            try {
                completionDateTime = LocalDateTime.parse(completionDateStr);
            } catch (Exception e) {
                // If that fails, try to parse as LocalDate and convert to LocalDateTime
                completionDateTime = LocalDate.parse(completionDateStr).atStartOfDay();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4000);
            response.put("message", "Invalid completion date format");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Create report using builder pattern
        ReportBuilder reportBuilder = new RepairReportBuilder();
        Report report;
        try {
            reportBuilder = reportBuilder
                    .withRepairDetails(repairDetails)
                    .withRepairSummary(resolutionSummary)
                    .withCompletionDateTime(completionDateTime);
            report = reportBuilder.build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4000);
            response.put("message", "Invalid report data: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            ServiceRequest updatedRequest = serviceRequestService.createReport(
                    serviceRequestId, report, technicianId);

            // Build response
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> reportResponse = new HashMap<>();

            Report createdReport = updatedRequest.getReport();
            reportResponse.put("id", createdReport.getId());
            reportResponse.put("serviceRequestId", serviceRequestId.toString());
            reportResponse.put("repairDetails", createdReport.getRepairDetails());
            reportResponse.put("resolutionSummary", createdReport.getRepairSummary());
            reportResponse.put("completionDate", createdReport.getCompletionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            reportResponse.put("createdAt", createdReport.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            response.put("report", reportResponse);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4040);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4090);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    /**
     * Get reports with filtering options (admin only)
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReports(
            @RequestParam(required = false) String date_start,
            @RequestParam(required = false) String date_end,
            @RequestParam(required = false) UUID technicianId,
            @RequestParam(required = false) UUID reportId) {

        // Get reports based on parameters
        List<Report> reports;

        try {
            if (reportId != null) {
                try {
                    Report report = reportService.getReportById(reportId);
                    reports = Collections.singletonList(report);
                } catch (IllegalArgumentException e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("errorCode", 4041);
                    response.put("message", "Report not found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
            } else if (technicianId != null) {
                try {
                    reports = reportService.getReportsByTechnician(technicianId);
                } catch (IllegalArgumentException e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("errorCode", 4040);
                    response.put("message", "Technician not found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
            } else if (date_start != null && date_end != null) {
                try {
                    LocalDate startDate = LocalDate.parse(date_start);
                    LocalDate endDate = LocalDate.parse(date_end);
                    reports = reportService.getReportsByDateRange(startDate, endDate);
                } catch (DateTimeParseException e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("errorCode", e.toString().contains(date_start) ? 4000 : 4001);
                    response.put("message", "Invalid date format. Expected format: YYYY-MM-DD");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                reports = reportService.getAllReports();
            }

            // Build response
            List<Map<String, Object>> reportsList = new ArrayList<>();
            for (Report report : reports) {
                Map<String, Object> reportMap = new HashMap<>();
                reportMap.put("id", report.getId());

                // Service request info
                Map<String, Object> serviceRequestMap = new HashMap<>();
                serviceRequestMap.put("id", report.getServiceRequest().getId());
                serviceRequestMap.put("status", report.getServiceRequest().getStateName());

                // Customer info
                Map<String, Object> customerMap = new HashMap<>();
                customerMap.put("id", report.getServiceRequest().getCustomer().getId());
                customerMap.put("fullName", report.getServiceRequest().getCustomer().getFullName());
                serviceRequestMap.put("customer", customerMap);

                // Item info
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", report.getServiceRequest().getItem().getName());
                itemMap.put("condition", report.getServiceRequest().getItem().getCondition());
                itemMap.put("issueDescription", report.getServiceRequest().getItem().getIssueDescription());
                serviceRequestMap.put("item", itemMap);

                reportMap.put("serviceRequest", serviceRequestMap);

                // Technician info
                Map<String, Object> technicianMap = new HashMap<>();
                technicianMap.put("id", report.getServiceRequest().getTechnician().getId());
                technicianMap.put("fullName", report.getServiceRequest().getTechnician().getFullName());
                reportMap.put("technician", technicianMap);

                // Report details
                reportMap.put("repairDetails", report.getRepairDetails());
                reportMap.put("resolutionSummary", report.getRepairSummary());
                reportMap.put("completionDate", report.getCompletionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                reportMap.put("createdAt", report.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                reportsList.add(reportMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("reports", reportsList);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 5000);
            response.put("message", "Internal server error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a technician's reports (technician only)
     */
    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasRole('TECHNICIAN') and authentication.principal.id == #technicianId")
    public ResponseEntity<?> getTechnicianReports(@PathVariable UUID technicianId) {
        try {
            List<Report> reports = reportService.getReportsByTechnician(technicianId);

            // Build response similar to admin's report view, but with less detail
            List<Map<String, Object>> reportsList = new ArrayList<>();
            for (Report report : reports) {
                Map<String, Object> reportMap = new HashMap<>();
                reportMap.put("id", report.getId());
                reportMap.put("serviceRequestId", report.getServiceRequest().getId());
                reportMap.put("repairDetails", report.getRepairDetails());
                reportMap.put("resolutionSummary", report.getRepairSummary());
                reportMap.put("completionDate", report.getCompletionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                reportMap.put("createdAt", report.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                reportsList.add(reportMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("reports", reportsList);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 5000);
            response.put("message", "Error retrieving reports: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}