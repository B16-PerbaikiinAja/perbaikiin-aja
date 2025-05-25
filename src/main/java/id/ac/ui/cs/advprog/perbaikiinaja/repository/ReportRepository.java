package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Report entities.
 */
@Repository
public interface ReportRepository extends CrudRepository<Report, UUID> {
}