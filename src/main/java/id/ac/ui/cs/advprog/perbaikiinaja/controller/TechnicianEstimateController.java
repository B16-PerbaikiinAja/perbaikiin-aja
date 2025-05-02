package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/technician/service-requests")
public class TechnicianEstimateController {

    private final ServiceRequestService serviceRequestService;

    @Autowired
    public TechnicianEstimateController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping("/{serviceRequestId}/estimate")
    public ResponseEntity<?> createEstimate(
            @PathVariable UUID serviceRequestId,
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

        // Validate estimate data
        Double estimatedCost = ((Number) requestBody.get("estimatedCost")).doubleValue();
        if (estimatedCost < 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4000);
            response.put("message", "Estimated cost cannot be negative");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        LocalDate completionDate = LocalDate.parse((String) requestBody.get("estimatedCompletionTime"));
        if (completionDate.isBefore(LocalDate.now())) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4001);
            response.put("message", "Estimated completion date cannot be in the past");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Create estimate
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(estimatedCost);
        estimate.setCompletionDate(completionDate);

        if (requestBody.containsKey("notes")) {
            estimate.setNotes((String) requestBody.get("notes"));
        }

        try {
            ServiceRequest updatedRequest = serviceRequestService.provideEstimate(
                    serviceRequestId, estimate, technicianId);

            // Build response
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> estimateResponse = new HashMap<>();

            RepairEstimate createdEstimate = updatedRequest.getEstimate();
            estimateResponse.put("id", createdEstimate.getId());
            estimateResponse.put("serviceRequestId", serviceRequestId);
            estimateResponse.put("estimatedCost", createdEstimate.getCost());
            estimateResponse.put("estimatedCompletionTime", createdEstimate.getCompletionDate());
            estimateResponse.put("notes", createdEstimate.getNotes());
            estimateResponse.put("status", "PENDING");
            estimateResponse.put("createdAt", createdEstimate.getCreatedDate());

            response.put("estimate", estimateResponse);

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