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

    private static final String serviceRequestIdStr = "serviceRequestId";
    private static final String repairDetailsStr = "repairDetails";
    private static final String resolutionSummaryStr = "resolutionSummary";
    private static final String estimateCompletionDateStr = "completionDate";
    private static final String messageStr = "message";
    private static final String errorCodeStr = "errorCode";
    private static final String createdAtStr = "createdAt";

    /**
     * Create a report as a technician
     */
    @PostMapping("/technician")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Map<String, Object>> createReport(
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        UUID technicianId = currentUser.getId();

        // Validate required fields
        if (!requestBody.containsKey(serviceRequestIdStr) ||
                !requestBody.containsKey(repairDetailsStr) ||
                !requestBody.containsKey(resolutionSummaryStr) ||
                !requestBody.containsKey(estimateCompletionDateStr)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UUID serviceRequestId;
        try {
            serviceRequestId = UUID.fromString((String) requestBody.get(serviceRequestIdStr));
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(errorCodeStr, 4000);
            response.put(messageStr, "Invalid service request ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String repairDetails = (String) requestBody.get(repairDetailsStr);
        String resolutionSummary = (String) requestBody.get(resolutionSummaryStr);

        // Parse completion date
        LocalDateTime completionDateTime;
        try {
            String completionDateStr = (String) requestBody.get(estimateCompletionDateStr);
            // Try to parse as LocalDateTime
            try {
                completionDateTime = LocalDateTime.parse(completionDateStr);
            } catch (Exception e) {
                // If that fails, try to parse as LocalDate and convert to LocalDateTime
                completionDateTime = LocalDate.parse(completionDateStr).atStartOfDay();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put(errorCodeStr, 4000);
            response.put(messageStr, "Invalid completion date format");
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
            response.put(errorCodeStr, 4000);
            response.put(messageStr, "Invalid report data: " + e.getMessage());
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
            reportResponse.put(serviceRequestIdStr, serviceRequestId.toString());
            reportResponse.put(repairDetailsStr, createdReport.getRepairDetails());
            reportResponse.put(resolutionSummaryStr, createdReport.getRepairSummary());
            reportResponse.put(estimateCompletionDateStr, createdReport.getCompletionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            reportResponse.put(createdAtStr, createdReport.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            response.put("report", reportResponse);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(errorCodeStr, 4040);
            response.put(messageStr, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(errorCodeStr, 4090);
            response.put(messageStr, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    /**
     * Get reports with filtering options (admin only)
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getReports(
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd,
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
                    response.put(errorCodeStr, 4041);
                    response.put(messageStr, "Report not found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
            } else if (technicianId != null) {
                try {
                    reports = reportService.getReportsByTechnician(technicianId);
                } catch (IllegalArgumentException e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put(errorCodeStr, 4040);
                    response.put(messageStr, "Technician not found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
            } else if (dateStart != null && dateEnd != null) {
                try {
                    LocalDate startDate = LocalDate.parse(dateStart);
                    LocalDate endDate = LocalDate.parse(dateEnd);
                    reports = reportService.getReportsByDateRange(startDate, endDate);
                } catch (DateTimeParseException e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put(errorCodeStr, e.toString().contains(dateStart) ? 4000 : 4001);
                    response.put(messageStr, "Invalid date format. Expected format: YYYY-MM-DD");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                reports = reportService.getAllReports();
            }

            // Build response - now we need to get ServiceRequest for each report
            List<Map<String, Object>> reportsList = new ArrayList<>();
            for (Report report : reports) {
                Map<String, Object> reportMap = new HashMap<>();
                reportMap.put("id", report.getId());

                // Get ServiceRequest for this report
                ServiceRequest serviceRequest = reportService.getServiceRequestByReportId(report.getId());

                // Service request info
                Map<String, Object> serviceRequestMap = new HashMap<>();
                serviceRequestMap.put("id", serviceRequest.getId());
                serviceRequestMap.put("status", serviceRequest.getStateType());

                // Customer info
                Map<String, Object> customerMap = new HashMap<>();
                customerMap.put("id", serviceRequest.getCustomer().getId());
                customerMap.put("fullName", serviceRequest.getCustomer().getFullName());
                serviceRequestMap.put("customer", customerMap);

                // Item info
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", serviceRequest.getItem().getName());
                itemMap.put("condition", serviceRequest.getItem().getCondition());
                itemMap.put("issueDescription", serviceRequest.getItem().getIssueDescription());
                serviceRequestMap.put("item", itemMap);

                reportMap.put("serviceRequest", serviceRequestMap);

                // Technician info
                Map<String, Object> technicianMap = new HashMap<>();
                technicianMap.put("id", serviceRequest.getTechnician().getId());
                technicianMap.put("fullName", serviceRequest.getTechnician().getFullName());
                reportMap.put("technician", technicianMap);

                // Report details
                reportMap.put(repairDetailsStr, report.getRepairDetails());
                reportMap.put(resolutionSummaryStr, report.getRepairSummary());
                reportMap.put(estimateCompletionDateStr, report.getCompletionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                reportMap.put(createdAtStr, report.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                reportsList.add(reportMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("reports", reportsList);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put(errorCodeStr, 5000);
            response.put(messageStr, "Internal server error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a technician's reports (technician only)
     */
    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasRole('TECHNICIAN') and authentication.principal.id == #technicianId")
    public ResponseEntity<Map<String, Object>> getTechnicianReports(@PathVariable UUID technicianId) {
        try {
            List<Report> reports = reportService.getReportsByTechnician(technicianId);

            // Build response similar to admin's report view, but with less detail
            List<Map<String, Object>> reportsList = new ArrayList<>();
            for (Report report : reports) {
                Map<String, Object> reportMap = new HashMap<>();
                reportMap.put("id", report.getId());
                reportMap.put(serviceRequestIdStr, reportService.getServiceRequestByReportId(report.getId()).getId());
                reportMap.put(repairDetailsStr, report.getRepairDetails());
                reportMap.put(resolutionSummaryStr, report.getRepairSummary());
                reportMap.put(estimateCompletionDateStr, report.getCompletionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                reportMap.put(createdAtStr, report.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                reportsList.add(reportMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("reports", reportsList);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put(errorCodeStr, 5000);
            response.put(messageStr, "Error retrieving reports: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}