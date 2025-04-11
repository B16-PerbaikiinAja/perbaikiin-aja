package id.ac.ui.cs.advprog.perbaikiinaja.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.perbaikiinaja.state.EstimatedState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.CustomerRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.ServiceRequestRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.TechnicianRepository;

/**
 * Implementation of the ServiceRequestService interface.
 * Provides business logic for managing service requests.
 */
@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CustomerRepository customerRepository;
    private final TechnicianRepository technicianRepository;

    @Autowired
    public ServiceRequestServiceImpl(
            ServiceRequestRepository serviceRequestRepository,
            CustomerRepository customerRepository,
            TechnicianRepository technicianRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.customerRepository = customerRepository;
        this.technicianRepository = technicianRepository;
    }

    @Override
    public List<ServiceRequest> findByTechnician(UUID technicianId) {
        return serviceRequestRepository.findByTechnicianId(technicianId);
    }

    @Override
    public List<ServiceRequest> findByCustomer(UUID customerId) {
        return serviceRequestRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<ServiceRequest> findById(UUID requestId) {
        return serviceRequestRepository.findById(requestId);
    }

    @Override
    public ServiceRequest provideEstimate(UUID requestId, RepairEstimate estimate, UUID technicianId) {
        ServiceRequest request = getServiceRequest(requestId);
        Technician technician = getTechnician(technicianId);

        // Ensure the technician is assigned to the request
        if (request.getTechnician() == null) {
            request.setTechnician(technician);
        } else if (!request.getTechnician().getId().equals(technicianId)) {
            throw new IllegalArgumentException("This technician is not assigned to this service request");
        }

        // Provide the estimate
        request.provideEstimate(estimate);

        return serviceRequestRepository.save(request);
    }

    @Override
    public ServiceRequest acceptEstimate(UUID requestId, UUID customerId) {
        ServiceRequest request = getServiceRequest(requestId);

        // First check if the state transition is valid
        if (!(request.getState() instanceof EstimatedState)) {
            throw new IllegalStateException("Cannot accept estimate in current state");
        }

        Customer customer = getCustomer(customerId);

        // Then check if the customer owns the request
        if (!request.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("This customer does not own this service request");
        }

        // Accept the estimate
        request.acceptEstimate();

        return serviceRequestRepository.save(request);
    }

    @Override
    public ServiceRequest rejectEstimate(UUID requestId, UUID customerId) {
        ServiceRequest request = getServiceRequest(requestId);
        Customer customer = getCustomer(customerId);

        // Ensure the customer owns the request
        if (!request.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("This customer does not own this service request");
        }

        // Reject the estimate
        request.rejectEstimate();

        return serviceRequestRepository.save(request);
    }

    @Override
    public ServiceRequest startService(UUID requestId, UUID technicianId) {
        ServiceRequest request = getServiceRequest(requestId);
        Technician technician = getTechnician(technicianId);

        // Ensure the technician is assigned to the request
        if (!request.getTechnician().getId().equals(technicianId)) {
            throw new IllegalArgumentException("This technician is not assigned to this service request");
        }

        // Start the service
        request.startService();

        return serviceRequestRepository.save(request);
    }

    @Override
    public ServiceRequest completeService(UUID requestId, UUID technicianId) {
        ServiceRequest request = getServiceRequest(requestId);
        Technician technician = getTechnician(technicianId);

        // Ensure the technician is assigned to the request
        if (!request.getTechnician().getId().equals(technicianId)) {
            throw new IllegalArgumentException("This technician is not assigned to this service request");
        }

        // Complete the service
        request.completeService();

        return serviceRequestRepository.save(request);
    }

    @Override
    public ServiceRequest createReport(UUID requestId, Report report, UUID technicianId) {
        ServiceRequest request = getServiceRequest(requestId);
        Technician technician = getTechnician(technicianId);

        // Ensure the technician is assigned to the request
        if (!request.getTechnician().getId().equals(technicianId)) {
            throw new IllegalArgumentException("This technician is not assigned to this service request");
        }

        // Create the report
        request.createReport(report);

        return serviceRequestRepository.save(request);
    }

    /**
     * Helper method to get a service request by ID or throw an exception.
     */
    private ServiceRequest getServiceRequest(UUID requestId) {
        return serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Service request not found with ID: " + requestId));
    }

    /**
     * Helper method to get a customer by ID or throw an exception.
     */
    private Customer getCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
    }

    /**
     * Helper method to get a technician by ID or throw an exception.
     */
    private Technician getTechnician(UUID technicianId) {
        return technicianRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found with ID: " + technicianId));
    }
}