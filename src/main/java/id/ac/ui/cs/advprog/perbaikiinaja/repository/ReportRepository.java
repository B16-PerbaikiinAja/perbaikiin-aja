package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Report entities.
 */
@Repository
public interface ReportRepository extends CrudRepository<Report, UUID> {
    /**
     * Find a report by service request ID
     * @param serviceRequestId The ID of the service request
     * @return The report, if found
     */
    Report findByServiceRequestId(UUID serviceRequestId);

    /**
     * Find reports by technician ID
     * @param technicianId The ID of the technician
     * @return List of reports for the technician
     */
    List<Report> findByServiceRequest_TechnicianId(UUID technicianId);

    /**
     * Find reports by completion date range
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of reports within the date range
     */
    List<Report> findByCompletionDateTimeBetween(LocalDate startDate, LocalDate endDate);
}