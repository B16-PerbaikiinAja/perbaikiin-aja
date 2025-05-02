package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/admin/report")
public class AdminReportController {

    private final ReportService reportService;

    @Autowired
    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<?> getReports(
            @RequestParam(required = false) String date_start,
            @RequestParam(required = false) String date_end,
            @RequestParam(required = false) UUID technicianId,
            @RequestParam(required = false) UUID reportId,
            Authentication authentication) {

        // Check if user is admin
        User currentUser = (User) authentication.getPrincipal();
        if (!"ADMIN".equals(currentUser.getRole())) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4030);
            response.put("message", "User is not an admin");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

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
                // Add more service request details as needed

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
}