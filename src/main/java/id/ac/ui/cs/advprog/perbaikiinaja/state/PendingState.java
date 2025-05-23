package id.ac.ui.cs.advprog.perbaikiinaja.state;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

/**
 * Represents the pending state of a service request.
 * In this state, the request has been created but no estimate has been provided yet.
 */
public class PendingState implements ServiceRequestState {

    @Override
    public ServiceRequestState provideEstimate(ServiceRequest request, RepairEstimate estimate) {
        if (estimate == null || !estimate.isValid()) {
            throw new IllegalArgumentException("Estimate must be valid");
        }

        request.setEstimate(estimate);
        return new EstimatedState();
    }

    @Override
    public ServiceRequestState acceptEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot accept estimate in pending state: no estimate has been provided yet");
    }

    @Override
    public ServiceRequestState rejectEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot reject estimate in pending state: no estimate has been provided yet");
    }

    @Override
    public ServiceRequestState startService(ServiceRequest request) {
        throw new IllegalStateException("Cannot start service in pending state: estimate must be provided and accepted first");
    }

    @Override
    public ServiceRequestState completeService(ServiceRequest request) {
        throw new IllegalStateException("Service in pending state cannot be completed: estimate must be provided and accepted first");
    }

    @Override
    public void createReport(ServiceRequest request, Report report) {
        throw new IllegalStateException("Cannot create report in pending state: service must be completed first");
    }

    @Override
    public ServiceRequestStateType getStateType() {
        return ServiceRequestStateType.PENDING;
    }
}