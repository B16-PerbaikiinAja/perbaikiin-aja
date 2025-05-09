package id.ac.ui.cs.advprog.perbaikiinaja.state;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

/**
 * Represents the estimated state of a service request.
 * In this state, an estimate has been provided but not yet accepted or rejected.
 */
public class EstimatedState implements ServiceRequestState {

    @Override
    public ServiceRequestState provideEstimate(ServiceRequest request, RepairEstimate estimate) {
        if (estimate == null || !estimate.isValid()) {
            throw new IllegalArgumentException("Estimate must be valid");
        }

        // Update the estimate
        request.setEstimate(estimate);
        return this; // Stay in the same state
    }

    @Override
    public ServiceRequestState acceptEstimate(ServiceRequest request) {
        // Ensure estimate exists
        if (request.getEstimate() == null) {
            throw new IllegalStateException("Cannot accept estimate: no estimate found");
        }

        return new AcceptedState();
    }

    @Override
    public ServiceRequestState rejectEstimate(ServiceRequest request) {
        // Ensure estimate exists
        if (request.getEstimate() == null) {
            throw new IllegalStateException("Cannot reject estimate: no estimate found");
        }

        return new RejectedState();
    }

    @Override
    public ServiceRequestState startService(ServiceRequest request) {
        throw new IllegalStateException("Cannot start service in estimated state: estimate must be accepted first");
    }

    @Override
    public ServiceRequestState completeService(ServiceRequest request) {
        throw new IllegalStateException("Cannot complete service in estimated state: estimate must be accepted and service started first");
    }

    @Override
    public void createReport(ServiceRequest request, Report report) {
        throw new IllegalStateException("Cannot create report in estimated state: service must be completed first");
    }

    @Override
    public String getStateName() {
        return "ESTIMATED";
    }
}