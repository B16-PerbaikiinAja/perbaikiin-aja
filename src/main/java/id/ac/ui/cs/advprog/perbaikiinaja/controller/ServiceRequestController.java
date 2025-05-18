package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/service-requests")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final Set<ServiceRequestStateType> validStatusValues =
            EnumSet.allOf(ServiceRequestStateType.class);

    @Autowired
    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    /**
     * Get service requests for a technician, optionally filtered by status
     */
    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasRole('TECHNICIAN') and authentication.principal.id == #technicianId")
    public ResponseEntity<?> getTechnicianServiceRequests(
            @PathVariable UUID technicianId,
            @RequestParam(required = false) ServiceRequestStateType status) {

        // Validate status parameter if provided
        if (status != null && !validStatusValues.contains(status)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", "INVALID_STATUS_PARAMETER");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Get service requests (filtered by status if provided)
        List<ServiceRequest> serviceRequests;
        if (status != null) {
            serviceRequests = serviceRequestService.findByTechnicianAndStatus(technicianId, status);
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

    /**
     * Get service requests for a customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') and authentication.principal.id == #customerId")
    public ResponseEntity<?> getCustomerServiceRequests(@PathVariable UUID customerId) {
        List<ServiceRequest> serviceRequests = serviceRequestService.findByCustomer(customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "SUCCESS");
        response.put("serviceRequests", serviceRequests);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update the status of a service request as a technician
     */
    @PutMapping("/{serviceRequestId}/technician/status")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<?> updateServiceRequestStatus(
            @PathVariable UUID serviceRequestId,
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
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
        ServiceRequestStateType status = null;
        try {
            if (requestBody.get("status") instanceof String) {
                // Convert String to enum
                status = ServiceRequestStateType.valueOf((String) requestBody.get("status"));
            } else {
                // Try direct cast (for test cases that may pass the actual enum)
                status = (ServiceRequestStateType) requestBody.get("status");
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            // Handles both invalid enum strings and casting errors
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4000);
            response.put("message", "Invalid status. Valid values are: " + Arrays.toString(ServiceRequestStateType.values()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Set<ServiceRequestStateType> validTechnicianStatusValues =
                new HashSet<>(Arrays.asList(ServiceRequestStateType.IN_PROGRESS, ServiceRequestStateType.COMPLETED));
        if (status == null || !validTechnicianStatusValues.contains(status)) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4000);
            response.put("message", "Invalid status. Valid values are: " + validTechnicianStatusValues);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // If status is COMPLETED, finalPrice is required
        if (ServiceRequestStateType.COMPLETED.equals(status) && !requestBody.containsKey("finalPrice")) {
            Map<String, Object> response = new HashMap<>();
            response.put("errorCode", 4001);
            response.put("message", "Final price is required when status is COMPLETED");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            ServiceRequest updatedRequest;
            if (ServiceRequestStateType.COMPLETED.equals(status)) {
                updatedRequest = serviceRequestService.completeService(serviceRequestId, technicianId);
            } else { // ServiceRequestStateType.IN_PROGRESS
                updatedRequest = serviceRequestService.startService(serviceRequestId, technicianId);
            }

            // Build response
            Map<String, Object> response = new HashMap<>();

            // Service request info
            Map<String, Object> serviceRequestResponse = new HashMap<>();
            serviceRequestResponse.put("id", updatedRequest.getId());
            serviceRequestResponse.put("status", updatedRequest.getStateType());
            if (ServiceRequestStateType.COMPLETED.equals(status)) {
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

    /**
     * Get all service requests (admin only)
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllServiceRequests() {
        List<ServiceRequest> serviceRequests = new ArrayList<>();
        // You would need to implement a method to get all service requests
        // serviceRequests = serviceRequestService.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "SUCCESS");
        response.put("serviceRequests", serviceRequests);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}