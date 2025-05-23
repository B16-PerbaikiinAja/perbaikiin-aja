package id.ac.ui.cs.advprog.perbaikiinaja.state;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

/**
 * Represents the completed state of a service request.
 * In this state, the service has been completed by the technician.
 * This is a semi-terminal state (only report creation is allowed).
 */
public class CompletedState implements ServiceRequestState {

    @Override
    public ServiceRequestState provideEstimate(ServiceRequest request, RepairEstimate estimate) {
        throw new IllegalStateException("Cannot provide estimate in completed state: service has already been completed");
    }

    @Override
    public ServiceRequestState acceptEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot accept estimate in completed state: service has already been completed");
    }

    @Override
    public ServiceRequestState rejectEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot reject estimate in completed state: service has already been completed");
    }

    @Override
    public ServiceRequestState startService(ServiceRequest request) {
        throw new IllegalStateException("Cannot start service in completed state: completed state cannot be changed");
    }

    @Override
    public ServiceRequestState completeService(ServiceRequest request) {
        throw new IllegalStateException("Cannot complete service in completed state: service has already been completed");
    }

    @Override
    public void createReport(ServiceRequest request, Report report) {
        if (report == null || !report.isValid()) {
            throw new IllegalArgumentException("Report must be valid");
        }

        request.setReport(report);
        // Stay in the same state, but now with a report
    }

    @Override
    public ServiceRequestStateType getStateType() {
        return ServiceRequestStateType.COMPLETED;
    }
}