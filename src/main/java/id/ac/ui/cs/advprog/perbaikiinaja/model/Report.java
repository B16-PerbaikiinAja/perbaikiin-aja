package id.ac.ui.cs.advprog.perbaikiinaja.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

/**
 * Represents a report for a completed service request.
 * Contains information about the repair that was performed.
 */

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 2000)
    private String repairDetails;

    @Column(nullable = false, length = 500)
    private String repairSummary;

    @Column(nullable = false)
    private LocalDateTime completionDateTime;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @OneToOne(mappedBy = "report")
    private ServiceRequest serviceRequest;

    public Report() {
        this.createdDateTime = LocalDateTime.now();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public String getRepairDetails() {
        return repairDetails;
    }

    public void setRepairDetails(String repairDetails) {
        this.repairDetails = repairDetails;
    }

    public String getRepairSummary() {
        return repairSummary;
    }

    public void setRepairSummary(String repairSummary) {
        this.repairSummary = repairSummary;
    }

    public LocalDateTime getCompletionDateTime() {
        return completionDateTime;
    }

    public void setCompletionDateTime(LocalDateTime completionDateTime) {
        this.completionDateTime = completionDateTime;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    /**
     * Validates that the report is complete with all required fields.
     * @return true if the report is valid, false otherwise
     */
    public boolean isValid() {
        // The issue might be here - let's make sure this actually returns false for invalid reports
        boolean hasRepairDetails = repairDetails != null && !repairDetails.isEmpty();
        boolean hasRepairSummary = repairSummary != null && !repairSummary.isEmpty();
        boolean hasCompletionDateTime = completionDateTime != null;

        return hasRepairDetails && hasRepairSummary && hasCompletionDateTime;
    }
}