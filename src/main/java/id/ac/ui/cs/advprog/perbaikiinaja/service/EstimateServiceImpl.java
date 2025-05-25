package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EstimateServiceImpl implements EstimateService {

    private final ServiceRequestRepository serviceRequestRepository;

    @Autowired
    public EstimateServiceImpl(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @Override
    public Optional<RepairEstimate> findById(UUID serviceRequestId) {
        Optional<ServiceRequest> serviceRequest = serviceRequestRepository.findById(serviceRequestId);
        if (serviceRequest.isPresent() && serviceRequest.get().getEstimate() != null) {
            return Optional.of(serviceRequest.get().getEstimate());
        }
        return Optional.empty();
    }

    @Override
    public ServiceRequest acceptEstimate(UUID serviceRequestId, UUID customerId, String feedback) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Service request not found with ID: " + serviceRequestId));

        if (serviceRequest.getEstimate() == null) {
            throw new IllegalArgumentException("No estimate available for service request: " + serviceRequestId);
        }

        // Check if customer is the owner of the service request
        if (!serviceRequest.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Customer is not the owner of this service request");
        }

        // Check if estimate is already accepted
        if (ServiceRequestStateType.ACCEPTED.equals(serviceRequest.getStateType()) ||
                ServiceRequestStateType.IN_PROGRESS.equals(serviceRequest.getStateType()) ||
                ServiceRequestStateType.COMPLETED.equals(serviceRequest.getStateType())) {
            throw new IllegalStateException("Estimate is already accepted");
        }

        // Accept the estimate
        serviceRequest.acceptEstimate();

        // Save the updated service request
        return serviceRequestRepository.save(serviceRequest);
    }

    @Override
    public UUID rejectEstimate(UUID serviceRequestId, UUID customerId, String feedback) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Service request not found with ID: " + serviceRequestId));

        if (serviceRequest.getEstimate() == null) {
            throw new IllegalArgumentException("No estimate available for service request: " + serviceRequestId);
        }

        // Check if customer is the owner of the service request
        if (!serviceRequest.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Customer is not the owner of this service request");
        }

        // Reject the estimate (in the state pattern, this transitions to REJECTED state)
        serviceRequest.rejectEstimate();

        // In a real implementation, we might not want to delete the service request
        // but keep it with a REJECTED state for record keeping
        // For now, we'll simulate deletion
        serviceRequestRepository.delete(serviceRequest);

        return serviceRequestId;
    }
}