package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.builder.ReportBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.builder.RepairReportBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
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

    private static final String SERVICEREQUESTIDSTR = "serviceRequestId";
    private static final String REPAIRDETAILSSTR = "repairDetails";
    private static final String RESOLUTIONSUMMARYSTR = "resolutionSummary";
    private static final String ESTIMATEDCOMPLETIONDATESTR = "completionDate";
    private static final String MESSAGESTR = "message";
    private static final String ERRORCODESTR = "errorCode";
    private static final String CREATEDATSTR = "createdAt";

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
        if (!requestBody.containsKey(SERVICEREQUESTIDSTR) ||
                !requestBody.containsKey(REPAIRDETAILSSTR) ||
                !requestBody.containsKey(RESOLUTIONSUMMARYSTR) ||
                !requestBody.containsKey(ESTIMATEDCOMPLETIONDATESTR)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UUID serviceRequestId;
        try {
            serviceRequestId = UUID.fromString((String) requestBody.get(SERVICEREQUESTIDSTR));
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERRORCODESTR, 4000);
            response.put(MESSAGESTR, "Invalid service request ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String repairDetails = (String) requestBody.get(REPAIRDETAILSSTR);
        String resolutionSummary = (String) requestBody.get(RESOLUTIONSUMMARYSTR);

        // Parse completion date
        LocalDateTime completionDateTime;
        try {
            String completionDateStr = (String) requestBody.get(ESTIMATEDCOMPLETIONDATESTR);
            // Try to parse as LocalDateTime
            try {
                completionDateTime = LocalDateTime.parse(completionDateStr);
            } catch (Exception e) {
                // If that fails, try to parse as LocalDate and convert to LocalDateTime
                completionDateTime = LocalDate.parse(completionDateStr).atStartOfDay();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERRORCODESTR, 4000);
            response.put(MESSAGESTR, "Invalid completion date format");
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
            response.put(ERRORCODESTR, 4000);
            response.put(MESSAGESTR, "Invalid report data: " + e.getMessage());
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
            reportResponse.put(SERVICEREQUESTIDSTR, serviceRequestId.toString());
            reportResponse.put(REPAIRDETAILSSTR, createdReport.getRepairDetails());
            reportResponse.put(RESOLUTIONSUMMARYSTR, createdReport.getRepairSummary());
            reportResponse.put(ESTIMATEDCOMPLETIONDATESTR, createdReport.getCompletionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            reportResponse.put(CREATEDATSTR, createdReport.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            response.put("report", reportResponse);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERRORCODESTR, 4040);
            response.put(MESSAGESTR, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERRORCODESTR, 4090);
            response.put(MESSAGESTR, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getReports(
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd,
            @RequestParam(required = false) UUID technicianId,
            @RequestParam(required = false) UUID reportId) {

        try {
            List<Report> reports = fetchReports(reportId, technicianId, dateStart, dateEnd);
            return ResponseEntity.ok(buildReportsResponse(reports));
        } catch (ResourceNotFoundException e) {
            return createErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidInputException e) {
            return createErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse(5000, "Internal server error: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches reports based on the given parameters.
     */
    private List<Report> fetchReports(UUID reportId, UUID technicianId, String dateStart, String dateEnd) {
        if (reportId != null) {
            return Collections.singletonList(getReportById(reportId));
        }

        if (technicianId != null) {
            return getReportsByTechnician(technicianId);
        }

        if (dateStart != null && dateEnd != null) {
            return getReportsByDateRange(dateStart, dateEnd);
        }

        return reportService.getAllReports();
    }

    /**
     * Gets a report by ID or throws an exception if not found.
     */
    private Report getReportById(UUID reportId) {
        try {
            return reportService.getReportById(reportId);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException(4041, "Report not found");
        }
    }

    /**
     * Gets reports by technician ID or throws an exception if technician not found.
     */
    private List<Report> getReportsByTechnician(UUID technicianId) {
        try {
            return reportService.getReportsByTechnician(technicianId);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException(4040, "Technician not found");
        }
    }

    /**
     * Gets reports within a date range or throws an exception if date format is invalid.
     */
    private List<Report> getReportsByDateRange(String dateStart, String dateEnd) {
        try {
            LocalDate startDate = LocalDate.parse(dateStart);
            LocalDate endDate = LocalDate.parse(dateEnd);
            return reportService.getReportsByDateRange(startDate, endDate);
        } catch (DateTimeParseException e) {
            int errorCode = e.toString().contains(dateStart) ? 4000 : 4001;
            throw new InvalidInputException(errorCode, "Invalid date format. Expected format: YYYY-MM-DD");
        }
    }

    /**
     * Builds the response object with the reports data.
     */
    private Map<String, Object> buildReportsResponse(List<Report> reports) {
        List<Map<String, Object>> reportsList = new ArrayList<>();

        for (Report report : reports) {
            Map<String, Object> reportMap = new HashMap<>();
            reportMap.put("id", report.getId());

            ServiceRequest serviceRequest = reportService.getServiceRequestByReportId(report.getId());

            reportMap.put("serviceRequest", buildServiceRequestMap(serviceRequest));
            reportMap.put("technician", buildTechnicianMap(serviceRequest.getTechnician()));

            // Report details
            reportMap.put(REPAIRDETAILSSTR, report.getRepairDetails());
            reportMap.put(RESOLUTIONSUMMARYSTR, report.getRepairSummary());
            reportMap.put(ESTIMATEDCOMPLETIONDATESTR, report.getCompletionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            reportMap.put(CREATEDATSTR, report.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            reportsList.add(reportMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("reports", reportsList);
        return response;
    }

    /**
     * Builds a service request map with customer and item info.
     */
    private Map<String, Object> buildServiceRequestMap(ServiceRequest serviceRequest) {
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

        return serviceRequestMap;
    }

    /**
     * Builds a technician info map.
     */
    private Map<String, Object> buildTechnicianMap(Technician technician) {
        Map<String, Object> technicianMap = new HashMap<>();
        technicianMap.put("id", technician.getId());
        technicianMap.put("fullName", technician.getFullName());
        return technicianMap;
    }

    /**
     * Creates an error response with the given error code, message, and status.
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(int errorCode, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put(ERRORCODESTR, errorCode);
        response.put(MESSAGESTR, message);
        return new ResponseEntity<>(response, status);
    }

    /**
     * Custom exception for resource not found errors.
     */
    private static class ResourceNotFoundException extends RuntimeException {
        private final int errorCode;

        public ResourceNotFoundException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    /**
     * Custom exception for invalid input errors.
     */
    private static class InvalidInputException extends RuntimeException {
        private final int errorCode;

        public InvalidInputException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
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
                reportMap.put(SERVICEREQUESTIDSTR, reportService.getServiceRequestByReportId(report.getId()).getId());
                reportMap.put(REPAIRDETAILSSTR, report.getRepairDetails());
                reportMap.put(RESOLUTIONSUMMARYSTR, report.getRepairSummary());
                reportMap.put(ESTIMATEDCOMPLETIONDATESTR, report.getCompletionDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                reportMap.put(CREATEDATSTR, report.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                reportsList.add(reportMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("reports", reportsList);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERRORCODESTR, 5000);
            response.put(MESSAGESTR, "Error retrieving reports: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}