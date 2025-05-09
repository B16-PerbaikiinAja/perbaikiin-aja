package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.dto.CustomerServiceRequestDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import id.ac.ui.cs.advprog.perbaikiinaja.services.coupon.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;

import java.util.UUID;

@RestController
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    @Autowired
    public CustomerServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/service-requests")
    public ResponseEntity<?> getServiceRequests(@AuthenticationPrincipal User user) {
        var requests = serviceRequestService.findByCustomer(user.getId());
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/service-request")
    public ResponseEntity<ServiceRequest> createServiceRequest(
            @RequestBody CustomerServiceRequestDto dto,
            @AuthenticationPrincipal User user
    ) {
        ServiceRequest created = serviceRequestService.createFromDto(dto, user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/service-request/{id}")
    public ResponseEntity<ServiceRequest> updateServiceRequest(
            @PathVariable UUID id,
            @RequestBody CustomerServiceRequestDto dto,
            @AuthenticationPrincipal User user
    ) {
        ServiceRequest updated = serviceRequestService.updateFromDto(id, dto, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/service-request/{id}")
    public ResponseEntity<Void> deleteServiceRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        serviceRequestService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}