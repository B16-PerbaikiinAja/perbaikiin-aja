package id.ac.ui.cs.advprog.perbaikiinaja.builder;

import java.time.LocalDateTime;

/**
 * Builder interface for creating Report objects.
 * Follows the Builder design pattern.
 */
public interface ReportBuilder {

    /**
     * Sets the repair details for the report.
     * @param repairDetails The detailed description of the repair
     * @return This builder for method chaining
     */
    ReportBuilder withRepairDetails(String repairDetails);

    /**
     * Sets the repair summary for the report.
     * @param repairSummary A brief summary of the repair
     * @return This builder for method chaining
     */
    ReportBuilder withRepairSummary(String repairSummary);

    /**
     * Sets the completion date and time for the report.
     * @param completionDateTime When the repair was completed
     * @return This builder for method chaining
     */
    ReportBuilder withCompletionDateTime(LocalDateTime completionDateTime);

    /**
     * Sets the completion date and time for the report to now.
     * @return This builder for method chaining
     */
    ReportBuilder completedNow();

    /**
     * Builds and returns a new Report instance.
     * @return A new Report with the configured properties
     * @throws IllegalStateException if the report is not valid
     */
    Report build() throws IllegalStateException;
}