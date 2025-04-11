package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Customer;

/**
 * Repository interface for Customer entities.
 */
@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {
    // Spring Data JPA will automatically provide implementation for the standard CRUD operations
    // Additional query methods can be defined here if needed
}