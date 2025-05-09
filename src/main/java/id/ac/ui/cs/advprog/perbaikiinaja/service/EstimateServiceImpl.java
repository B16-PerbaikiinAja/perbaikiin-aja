package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class EstimateServiceImpl implements EstimateService {

    private final ServiceRequestRepository serviceRequestRepository;

    @Autowired
    public EstimateServiceImpl(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @Override
    public Optional<RepairEstimate> findById(UUID estimateId) {
        // In a real implementation, there would be an EstimateRepository
        // For now, we'll search through all service requests to find the estimate
        return StreamSupport.stream(serviceRequestRepository.findAll().spliterator(), false)
                .filter(request -> request.getEstimate() != null)
                .filter(request -> request.getEstimate().getId().equals(estimateId))
                .map(ServiceRequest::getEstimate)
                .findFirst();
    }

    @Override
    public ServiceRequest getServiceRequest(RepairEstimate estimate) {
        // In a real implementation, the RepairEstimate would have a reference to its ServiceRequest
        // For now, we'll search through all service requests to find the one with this estimate
        return StreamSupport.stream(serviceRequestRepository.findAll().spliterator(), false)
                .filter(request -> request.getEstimate() != null)
                .filter(request -> request.getEstimate().getId().equals(estimate.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Service request not found for estimate: " + estimate.getId()));
    }

    @Override
    public ServiceRequest acceptEstimate(UUID estimateId, UUID customerId, String feedback) {
        RepairEstimate estimate = findById(estimateId)
                .orElseThrow(() -> new IllegalArgumentException("Estimate not found with ID: " + estimateId));

        ServiceRequest serviceRequest = getServiceRequest(estimate);

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

        // Set feedback (in a real implementation, this would be a field in the estimate)
        // For now, we'll just pretend it's stored

        // Save the updated service request
        return serviceRequestRepository.save(serviceRequest);
    }

    @Override
    public UUID rejectEstimate(UUID estimateId, UUID customerId, String feedback) {
        RepairEstimate estimate = findById(estimateId)
                .orElseThrow(() -> new IllegalArgumentException("Estimate not found with ID: " + estimateId));

        ServiceRequest serviceRequest = getServiceRequest(estimate);

        // Check if customer is the owner of the service request
        if (!serviceRequest.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Customer is not the owner of this service request");
        }

        // Store the service request ID before deleting
        UUID serviceRequestId = serviceRequest.getId();

        // Reject the estimate (in the state pattern, this transitions to REJECTED state)
        serviceRequest.rejectEstimate();

        // In a real implementation, we might not want to delete the service request
        // but keep it with a REJECTED state for record keeping
        // For now, we'll simulate deletion
        serviceRequestRepository.delete(serviceRequest);

        return serviceRequestId;
    }
}