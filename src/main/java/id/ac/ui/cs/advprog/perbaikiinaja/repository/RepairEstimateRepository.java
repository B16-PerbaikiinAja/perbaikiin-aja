package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for RepairEstimate entities.
 */
@Repository
public interface RepairEstimateRepository extends CrudRepository<RepairEstimate, UUID> {
    /**
     * Find a repair estimate by service request ID
     * @param serviceRequestId The ID of the service request
     * @return The repair estimate, if found
     */
    RepairEstimate findByServiceRequestId(UUID serviceRequestId);
}