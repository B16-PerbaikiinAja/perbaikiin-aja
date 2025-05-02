package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.service.EstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/customer/estimates")
public class CustomerEstimateController {

    private final EstimateService estimateService;

    @Autowired
    public CustomerEstimateController(EstimateService estimateService) {
        this.estimateService = estimateService;
    }

    @PutMapping("/{estimateId}/response")
    public ResponseEntity<?> respondToEstimate(
            @PathVariable UUID estimateId,
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {

        // Get authenticated user
        User currentUser = (User) authentication.getPrincipal();
        if (!"CUSTOMER".equals(currentUser.getRole())) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4010);
            response.put("message", "User is not a customer");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        UUID customerId = currentUser.getId();

        // Validate action
        String action = (String) requestBody.get("action");
        if (action == null || (!action.equals("ACCEPT") && !action.equals("REJECT"))) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4000);
            response.put("message", "Action must be ACCEPT or REJECT");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Get feedback (optional)
        String feedback = (String) requestBody.getOrDefault("feedback", "");

        // Find estimate
        Optional<RepairEstimate> estimateOpt = estimateService.findById(estimateId);
        if (estimateOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4040);
            response.put("message", "Estimate not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        RepairEstimate estimate = estimateOpt.get();

        // Check if customer owns the service request
        ServiceRequest serviceRequest;
        try {
            serviceRequest = estimateService.getServiceRequest(estimate);
            if (!serviceRequest.getCustomer().getId().equals(customerId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("errorCode", 4030);
                response.put("message", "User is not the owner of the service request");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4041);
            response.put("message", "Service request not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            // Process response based on action
            if ("ACCEPT".equals(action)) {
                serviceRequest = estimateService.acceptEstimate(estimateId, customerId, feedback);

                // Build response
                Map<String, Object> response = new HashMap<>();

                // Estimate info
                Map<String, Object> estimateResponse = new HashMap<>();
                estimateResponse.put("id", estimate.getId());
                estimateResponse.put("serviceRequestId", serviceRequest.getId());
                estimateResponse.put("estimatedCost", estimate.getCost());
                estimateResponse.put("estimatedCompletionTime", estimate.getCompletionDate());
                estimateResponse.put("status", "ACCEPTED");
                estimateResponse.put("feedback", feedback);
                estimateResponse.put("updatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                // Service request info
                Map<String, Object> serviceRequestResponse = new HashMap<>();
                serviceRequestResponse.put("id", serviceRequest.getId());
                serviceRequestResponse.put("status", serviceRequest.getStateName());

                response.put("estimate", estimateResponse);
                response.put("serviceRequest", serviceRequestResponse);

                return new ResponseEntity<>(response, HttpStatus.OK);

            } else { // REJECT
                UUID serviceRequestId = estimateService.rejectEstimate(estimateId, customerId, feedback);

                // Build response
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Estimate rejected and service request deleted successfully");
                response.put("estimateId", estimateId.toString());
                response.put("serviceRequestId", serviceRequestId.toString());

                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4090);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4000);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}