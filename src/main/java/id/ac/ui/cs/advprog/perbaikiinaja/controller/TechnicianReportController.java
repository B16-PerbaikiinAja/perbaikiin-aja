package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.builder.ReportBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.builder.RepairReportBuilder;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/technician/report")
public class TechnicianReportController {

    private final ServiceRequestService serviceRequestService;

    @Autowired
    public TechnicianReportController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {

        // Get authenticated user
        User currentUser = (User) authentication.getPrincipal();
        if (!"TECHNICIAN".equals(currentUser.getRole())) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4030);
            response.put("message", "User is not a technician");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

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
        ReportBuilder reportBuilder = new RepairReportBuilder()
                .withRepairDetails(repairDetails)
                .withRepairSummary(resolutionSummary)
                .withCompletionDateTime(completionDateTime);

        Report report;
        try {
            report = reportBuilder.build();
        } catch (IllegalStateException e) {
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
}