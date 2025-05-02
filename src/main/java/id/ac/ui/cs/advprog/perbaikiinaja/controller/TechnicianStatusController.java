package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/technician/service-requests")
public class TechnicianStatusController {

    private final ServiceRequestService serviceRequestService;
    private final Set<String> validStatusValues = new HashSet<>(Arrays.asList(
            "IN_PROGRESS", "COMPLETED"
    ));

    @Autowired
    public TechnicianStatusController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PutMapping("/{serviceRequestId}/status")
    public ResponseEntity<?> updateStatus(
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

        // Check if service request exists
        Optional<ServiceRequest> serviceRequestOpt = serviceRequestService.findById(serviceRequestId);
        if (serviceRequestOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4040);
            response.put("message", "Service request not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ServiceRequest serviceRequest = serviceRequestOpt.get();

        // Check if technician is assigned to this service request
        if (!serviceRequest.getTechnician().getId().equals(technicianId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4030);
            response.put("message", "User is not the assigned technician for this service request");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // Validate status
        String status = (String) requestBody.get("status");
        if (status == null || !validStatusValues.contains(status)) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4000);
            response.put("message", "Invalid status. Valid values are: " + String.join(", ", validStatusValues));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // If status is COMPLETED, finalPrice is required
        if ("COMPLETED".equals(status) && !requestBody.containsKey("finalPrice")) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4001);
            response.put("message", "Final price is required when status is COMPLETED");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            ServiceRequest updatedRequest;
            if ("COMPLETED".equals(status)) {
                updatedRequest = serviceRequestService.completeService(serviceRequestId, technicianId);
            } else { // "IN_PROGRESS"
                updatedRequest = serviceRequestService.startService(serviceRequestId, technicianId);
            }

            // Build response
            Map<String, Object> response = new HashMap<>();

            // Service request info
            Map<String, Object> serviceRequestResponse = new HashMap<>();
            serviceRequestResponse.put("id", updatedRequest.getId());
            serviceRequestResponse.put("status", updatedRequest.getStateName());
            if ("COMPLETED".equals(status)) {
                Number finalPrice = (Number) requestBody.get("finalPrice");
                serviceRequestResponse.put("finalPrice", finalPrice);
            }
            serviceRequestResponse.put("updatedAt", LocalDateTime.now());

            // Technician info
            Map<String, Object> technicianResponse = new HashMap<>();
            Technician technician = updatedRequest.getTechnician();
            technicianResponse.put("id", technician.getId());
            technicianResponse.put("completedJobsCount", technician.getCompletedJobCount());
            technicianResponse.put("totalEarnings", technician.getTotalEarnings());

            response.put("serviceRequest", serviceRequestResponse);
            response.put("technician", technicianResponse);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4002);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}