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

import java.util.*;

@RestController
@RequestMapping("/technician/service-requests")
public class TechnicianServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final Set<String> validStatusValues = new HashSet<>(Arrays.asList(
            "PENDING", "ESTIMATED", "ACCEPTED", "IN_PROGRESS", "COMPLETED", "REJECTED"
    ));

    @Autowired
    public TechnicianServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/{technicianId}")
    public ResponseEntity<?> getServiceRequests(
            @PathVariable UUID technicianId,
            @RequestParam(required = false) String status,
            Authentication authentication) {

        // Check if authenticated user is the same as requested technician ID
        User currentUser = (User) authentication.getPrincipal();
        if (!currentUser.getId().equals(technicianId) || !"TECHNICIAN".equals(currentUser.getRole())) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 403);
            response.put("message", "ACCESS_DENIED");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // Validate status parameter if provided
        if (status != null && !validStatusValues.contains(status.toUpperCase())) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", "INVALID_STATUS_PARAMETER");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Get service requests (filtered by status if provided)
        List<ServiceRequest> serviceRequests;
        if (status != null) {
            serviceRequests = serviceRequestService.findByTechnicianAndStatus(technicianId, status.toUpperCase());
        } else {
            serviceRequests = serviceRequestService.findByTechnician(technicianId);
        }

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "SUCCESS");
        response.put("serviceRequests", serviceRequests);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}