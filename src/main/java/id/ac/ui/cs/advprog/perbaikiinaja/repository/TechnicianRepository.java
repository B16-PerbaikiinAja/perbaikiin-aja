package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Technician;

/**
 * Repository interface for Technician entities.
 */
@Repository
public interface TechnicianRepository extends CrudRepository<Technician, UUID> {
    // Spring Data JPA will automatically provide implementation for the standard CRUD operations
    // Additional query methods can be defined here if needed
}