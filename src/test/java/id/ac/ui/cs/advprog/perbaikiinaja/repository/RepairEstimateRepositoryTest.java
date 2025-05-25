package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RepairEstimateRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RepairEstimateRepository repairEstimateRepository;

    @Test
    void testSave() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(150.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(5));
        estimate.setNotes("Test repair notes");

        // Act
        RepairEstimate savedEstimate = repairEstimateRepository.save(estimate);

        // Assert
        assertNotNull(savedEstimate);
        assertNotNull(savedEstimate.getId());
        assertEquals(150.0, savedEstimate.getCost());
        assertEquals(LocalDate.now().plusDays(5), savedEstimate.getCompletionDate());
        assertEquals("Test repair notes", savedEstimate.getNotes());
    }

    @Test
    void testFindById() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));
        estimate.setNotes("Test notes");

        entityManager.persist(estimate);
        entityManager.flush();

        // Act
        Optional<RepairEstimate> foundEstimate = repairEstimateRepository.findById(estimate.getId());

        // Assert
        assertTrue(foundEstimate.isPresent());
        assertEquals(estimate.getId(), foundEstimate.get().getId());
        assertEquals(100.0, foundEstimate.get().getCost());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<RepairEstimate> foundEstimate = repairEstimateRepository.findById(UUID.randomUUID());

        // Assert
        assertFalse(foundEstimate.isPresent());
    }

    @Test
    void testFindAll() {
        // Arrange
        RepairEstimate estimate1 = new RepairEstimate();
        estimate1.setCost(100.0);
        estimate1.setCompletionDate(LocalDate.now().plusDays(3));

        RepairEstimate estimate2 = new RepairEstimate();
        estimate2.setCost(200.0);
        estimate2.setCompletionDate(LocalDate.now().plusDays(5));

        entityManager.persist(estimate1);
        entityManager.persist(estimate2);
        entityManager.flush();

        // Act
        Iterable<RepairEstimate> estimates = repairEstimateRepository.findAll();
        List<RepairEstimate> estimateList = new ArrayList<>();
        estimates.forEach(estimateList::add);

        // Assert
        assertFalse(estimateList.isEmpty());
        assertTrue(estimateList.size() >= 2);
        assertTrue(estimateList.stream().anyMatch(e -> e.getId().equals(estimate1.getId())));
        assertTrue(estimateList.stream().anyMatch(e -> e.getId().equals(estimate2.getId())));
    }

    @Test
    void testDelete() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));

        entityManager.persist(estimate);
        entityManager.flush();

        // Act
        repairEstimateRepository.delete(estimate);
        Optional<RepairEstimate> deletedEstimate = repairEstimateRepository.findById(estimate.getId());

        // Assert
        assertFalse(deletedEstimate.isPresent());
    }

    @Test
    void testDeleteById() {
        // Arrange
        RepairEstimate estimate = new RepairEstimate();
        estimate.setCost(100.0);
        estimate.setCompletionDate(LocalDate.now().plusDays(3));

        entityManager.persist(estimate);
        entityManager.flush();

        // Act
        repairEstimateRepository.deleteById(estimate.getId());
        Optional<RepairEstimate> deletedEstimate = repairEstimateRepository.findById(estimate.getId());

        // Assert
        assertFalse(deletedEstimate.isPresent());
    }
}