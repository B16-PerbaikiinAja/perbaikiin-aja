package id.ac.ui.cs.advprog.perbaikiinaja.service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

/**
 * Service interface for managing service requests.
 * Provides a facade for the business logic related to service requests.
 */
public interface ServiceRequestService {

    /**
     * Retrieves all service requests assigned to a technician.
     * @param technicianId The ID of the technician
     * @return List of service requests
     */
    List<ServiceRequest> findByTechnician(UUID technicianId);

    /**
     * Retrieves all service requests from a customer.
     * @param customerId The ID of the customer
     * @return List of service requests
     */
    List<ServiceRequest> findByCustomer(UUID customerId);

    /**
     * Retrieves a service request by its ID.
     * @param requestId The ID of the service request
     * @return The service request, if found
     */
    Optional<ServiceRequest> findById(UUID requestId);

    /**
     * Provides an estimate for a service request.
     * @param requestId The ID of the service request
     * @param estimate The repair estimate
     * @param technicianId The ID of the technician providing the estimate
     * @return The updated service request
     * @throws IllegalStateException if the request is not in the pending state
     * @throws IllegalArgumentException if the estimate is invalid
     */
    ServiceRequest provideEstimate(UUID requestId, RepairEstimate estimate, UUID technicianId);

    /**
     * Accepts an estimate for a service request.
     * @param requestId The ID of the service request
     * @param customerId The ID of the customer
     * @return The updated service request
     * @throws IllegalStateException if the request is not in the estimated state
     */
    ServiceRequest acceptEstimate(UUID requestId, UUID customerId);

    /**
     * Rejects an estimate for a service request.
     * @param requestId The ID of the service request
     * @param customerId The ID of the customer
     * @return The updated service request
     * @throws IllegalStateException if the request is not in the estimated state
     */
    ServiceRequest rejectEstimate(UUID requestId, UUID customerId);

    /**
     * Starts the service for a request.
     * @param requestId The ID of the service request
     * @param technicianId The ID of the technician
     * @return The updated service request
     * @throws IllegalStateException if the request is not in the accepted state
     */
    ServiceRequest startService(UUID requestId, UUID technicianId);

    /**
     * Completes the service for a request.
     * @param requestId The ID of the service request
     * @param technicianId The ID of the technician
     * @return The updated service request
     * @throws IllegalStateException if the request is not in the in-progress state
     */
    ServiceRequest completeService(UUID requestId, UUID technicianId);

    /**
     * Creates a report for a completed service request.
     * @param requestId The ID of the service request
     * @param report The service report
     * @param technicianId The ID of the technician
     * @return The updated service request
     * @throws IllegalStateException if the request is not in the completed state
     * @throws IllegalArgumentException if the report is invalid
     */
    ServiceRequest createReport(UUID requestId, Report report, UUID technicianId);
}