package id.ac.ui.cs.advprog.perbaikiinaja.repository;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReportRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    void testSave() {
        // Arrange
        Report report = new Report();
        report.setRepairDetails("Replaced screen with a new one");
        report.setRepairSummary("Fixed the cracked screen");
        report.setCompletionDateTime(LocalDateTime.now());

        // Act
        Report savedReport = reportRepository.save(report);

        // Assert
        assertNotNull(savedReport);
        assertNotNull(savedReport.getId());
        assertEquals("Replaced screen with a new one", savedReport.getRepairDetails());
        assertEquals("Fixed the cracked screen", savedReport.getRepairSummary());
        assertNotNull(savedReport.getCompletionDateTime());
        assertNotNull(savedReport.getCreatedDateTime());
    }

    @Test
    void testFindById() {
        // Arrange
        Report report = new Report();
        report.setRepairDetails("Replaced motherboard");
        report.setRepairSummary("Fixed the power issue");
        report.setCompletionDateTime(LocalDateTime.now());

        entityManager.persist(report);
        entityManager.flush();

        // Act
        Optional<Report> foundReport = reportRepository.findById(report.getId());

        // Assert
        assertTrue(foundReport.isPresent());
        assertEquals(report.getId(), foundReport.get().getId());
        assertEquals("Replaced motherboard", foundReport.get().getRepairDetails());
        assertEquals("Fixed the power issue", foundReport.get().getRepairSummary());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<Report> foundReport = reportRepository.findById(UUID.randomUUID());

        // Assert
        assertFalse(foundReport.isPresent());
    }

    @Test
    void testFindAll() {
        // Arrange
        Report report1 = new Report();
        report1.setRepairDetails("Replaced screen");
        report1.setRepairSummary("Fixed the cracked screen");
        report1.setCompletionDateTime(LocalDateTime.now());

        Report report2 = new Report();
        report2.setRepairDetails("Replaced battery");
        report2.setRepairSummary("Fixed the battery drain issue");
        report2.setCompletionDateTime(LocalDateTime.now());

        entityManager.persist(report1);
        entityManager.persist(report2);
        entityManager.flush();

        // Act
        Iterable<Report> reports = reportRepository.findAll();
        List<Report> reportList = new ArrayList<>();
        reports.forEach(reportList::add);

        // Assert
        assertFalse(reportList.isEmpty());
        assertTrue(reportList.size() >= 2);
        assertTrue(reportList.stream().anyMatch(r -> r.getId().equals(report1.getId())));
        assertTrue(reportList.stream().anyMatch(r -> r.getId().equals(report2.getId())));
    }

    @Test
    void testDelete() {
        // Arrange
        Report report = new Report();
        report.setRepairDetails("Replaced screen");
        report.setRepairSummary("Fixed the cracked screen");
        report.setCompletionDateTime(LocalDateTime.now());

        entityManager.persist(report);
        entityManager.flush();

        // Act
        reportRepository.delete(report);
        Optional<Report> deletedReport = reportRepository.findById(report.getId());

        // Assert
        assertFalse(deletedReport.isPresent());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Report report = new Report();
        report.setRepairDetails("Replaced screen");
        report.setRepairSummary("Fixed the cracked screen");
        report.setCompletionDateTime(LocalDateTime.now());

        entityManager.persist(report);
        entityManager.flush();

        // Act
        reportRepository.deleteById(report.getId());
        Optional<Report> deletedReport = reportRepository.findById(report.getId());

        // Assert
        assertFalse(deletedReport.isPresent());
    }
}