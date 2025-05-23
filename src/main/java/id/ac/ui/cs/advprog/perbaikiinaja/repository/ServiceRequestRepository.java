package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Repository interface for ServiceRequest entities.
 */
@Repository
public interface ServiceRequestRepository extends CrudRepository<ServiceRequest, UUID> {

    /**
     * Finds all service requests assigned to a technician.
     * @param technicianId The ID of the technician
     * @return A list of service requests
     */
    List<ServiceRequest> findByTechnicianId(UUID technicianId);

    /**
     * Finds all service requests from a customer.
     * @param customerId The ID of the customer
     * @return A list of service requests
     */
    List<ServiceRequest> findByCustomerId(UUID customerId);
}