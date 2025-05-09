package id.ac.ui.cs.advprog.perbaikiinaja.builder;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

import java.time.LocalDateTime;

/**
 * Concrete implementation of ReportBuilder for creating repair reports.
 * Follows the Builder design pattern.
 */
public class RepairReportBuilder implements ReportBuilder {

    private Report report;

    public RepairReportBuilder() {
        this.report = new Report();
    }

    @Override
    public ReportBuilder withRepairDetails(String repairDetails) {
        if (repairDetails == null || repairDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Repair details cannot be null or empty");
        }
        report.setRepairDetails(repairDetails);
        return this;
    }

    @Override
    public ReportBuilder withRepairSummary(String repairSummary) {
        if (repairSummary == null || repairSummary.trim().isEmpty()) {
            throw new IllegalArgumentException("Repair summary cannot be null or empty");
        }
        report.setRepairSummary(repairSummary);
        return this;
    }

    @Override
    public ReportBuilder withCompletionDateTime(LocalDateTime completionDateTime) {
        if (completionDateTime == null) {
            throw new IllegalArgumentException("Completion date/time cannot be null");
        }
        if (completionDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Completion date/time cannot be in the future");
        }
        report.setCompletionDateTime(completionDateTime);
        return this;
    }

    @Override
    public ReportBuilder completedNow() {
        report.setCompletionDateTime(LocalDateTime.now());
        return this;
    }

    @Override
    public Report build() throws IllegalStateException {
        // Debug output to see if isValid is being called and what it returns
        boolean isReportValid = report.isValid();
        if (!isReportValid) {
            throw new IllegalStateException("Cannot build an invalid report. Ensure all required fields are set.");
        }
        return report;
    }

    /**
     * Resets the builder to create a new report.
     * @return This builder for method chaining
     */
    public ReportBuilder reset() {
        this.report = new Report();
        return this;
    }
}