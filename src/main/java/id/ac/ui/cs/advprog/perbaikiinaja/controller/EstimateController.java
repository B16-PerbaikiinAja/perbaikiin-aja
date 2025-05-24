package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.service.EstimateService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/estimates")
public class EstimateController {

    private final ServiceRequestService serviceRequestService;
    private final EstimateService estimateService;
    private final WalletService walletService;

    private final String MESSAGE_STR = "message";
    private final String ERR_STR = "errorCode";

    @Autowired
    public EstimateController(
            ServiceRequestService serviceRequestService, 
            EstimateService estimateService, 
            WalletService walletService) {
        this.serviceRequestService = serviceRequestService;
        this.estimateService = estimateService;
        this.walletService = walletService;
    }

    /**
     * Create an estimate for a service request as a technician
     */
    @PostMapping("/technician/service-requests/{serviceRequestId}")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<?> createEstimate(
            @PathVariable UUID serviceRequestId,
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        UUID technicianId = currentUser.getId();

        // Validate estimate data
        Double estimatedCost = ((Number) requestBody.get("estimatedCost")).doubleValue();
        if (estimatedCost < 0) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERR_STR, 4000);
            response.put(MESSAGE_STR, "Estimated cost cannot be negative");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        LocalDate completionDate = LocalDate.parse((String) requestBody.get("estimatedCompletionTime"));
        if (completionDate.isBefore(LocalDate.now())) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERR_STR, 4001);
            response.put(MESSAGE_STR, "Estimated completion date cannot be in the past");
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
            estimateResponse.put("status", ServiceRequestStateType.PENDING);
            estimateResponse.put("createdAt", createdEstimate.getCreatedDate());

            response.put("estimate", estimateResponse);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERR_STR, 4040);
            response.put(MESSAGE_STR, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERR_STR, 4090);
            response.put(MESSAGE_STR, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    /**
     * Respond to an estimate (accept/reject) as a customer
     */
    @PutMapping("/customer/{serviceRequestId}/response")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> respondToEstimate(
            @PathVariable UUID serviceRequestId,
            @RequestBody Map<String, Object> requestBody,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        UUID customerId = currentUser.getId();

        // Validate action
        String action = (String) requestBody.get("action");
        if (action == null || (!action.equals("ACCEPT") && !action.equals("REJECT"))) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERR_STR, 4000);
            response.put(MESSAGE_STR, "Action must be ACCEPT or REJECT");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Get feedback (optional)
        String feedback = (String) requestBody.getOrDefault("feedback", "");

        // Find service request and its estimate
        Optional<RepairEstimate> estimateOpt = estimateService.findById(serviceRequestId);
        if (estimateOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERR_STR, 4040);
            response.put(MESSAGE_STR, "Service request or estimate not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        RepairEstimate estimate = estimateOpt.get();

        // Try to get service request
        ServiceRequest serviceRequest;
        try {
            serviceRequest = serviceRequestService.findById(serviceRequestId)
                    .orElseThrow(() -> new IllegalArgumentException("Service request not found"));

            if (!serviceRequest.getCustomer().getId().equals(customerId)) {
                Map<String, Object> response = new HashMap<>();
                response.put(ERR_STR, 4030);
                response.put(MESSAGE_STR, "User is not the owner of the service request");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERR_STR, 4041);
            response.put(MESSAGE_STR, "Service request not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            // Process response based on action
            if ("ACCEPT".equals(action)) {

                // Check if customer has sufficient balance
                Optional<Wallet> customerWalletOpt = walletService.getWalletByUserId(customerId);
                if (customerWalletOpt.isPresent()) {
                    Wallet customerWallet = customerWalletOpt.get();
                    BigDecimal estimateAmount = BigDecimal.valueOf(estimate.getCost());

                    if (customerWallet.getBalance().compareTo(estimateAmount) < 0) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("errorCode", 4002);
                        response.put("message", "Insufficient funds in wallet. Please deposit funds before accepting the estimate.");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }

                    serviceRequest = estimateService.acceptEstimate(serviceRequestId, customerId, feedback);
                }

                // Build response
                Map<String, Object> response = new HashMap<>();

                // Estimate info
                Map<String, Object> estimateResponse = new HashMap<>();
                estimateResponse.put("id", estimate.getId());
                estimateResponse.put("serviceRequestId", serviceRequestId);
                estimateResponse.put("estimatedCost", estimate.getCost());
                estimateResponse.put("estimatedCompletionTime", estimate.getCompletionDate());
                estimateResponse.put("status", ServiceRequestStateType.ACCEPTED);
                estimateResponse.put("feedback", feedback);
                estimateResponse.put("updatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                // Service request info
                Map<String, Object> serviceRequestResponse = new HashMap<>();
                serviceRequestResponse.put("id", serviceRequest.getId());
                serviceRequestResponse.put("status", serviceRequest.getStateType());

                response.put("estimate", estimateResponse);
                response.put("serviceRequest", serviceRequestResponse);

                return new ResponseEntity<>(response, HttpStatus.OK);

            } else { // REJECT
                UUID rejectedServiceRequestId = estimateService.rejectEstimate(serviceRequestId, customerId, feedback);

                // Build response
                Map<String, Object> response = new HashMap<>();
                response.put(MESSAGE_STR, "Estimate rejected and service request deleted successfully");
                response.put("serviceRequestId", serviceRequestId.toString());

                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERR_STR, 4090);
            response.put(MESSAGE_STR, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(ERR_STR, 4000);
            response.put(MESSAGE_STR, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}