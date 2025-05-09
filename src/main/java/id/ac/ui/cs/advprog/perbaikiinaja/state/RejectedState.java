package id.ac.ui.cs.advprog.perbaikiinaja.state;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

/**
 * Represents the rejected state of a service request.
 * In this state, the estimate has been rejected by the customer and the request is terminated.
 * This is a terminal state.
 */
public class RejectedState implements ServiceRequestState {

    @Override
    public ServiceRequestState provideEstimate(ServiceRequest request, RepairEstimate estimate) {
        throw new IllegalStateException("Cannot provide estimate in rejected state: request in rejected state cannot be changed");
    }

    @Override
    public ServiceRequestState acceptEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot accept estimate in rejected state: request in rejected state cannot be changed");
    }

    @Override
    public ServiceRequestState rejectEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot reject estimate in rejected state: request in rejected state cannot be changed");
    }

    @Override
    public ServiceRequestState startService(ServiceRequest request) {
        throw new IllegalStateException("Cannot start service in rejected state: request in rejected state cannot be changed");
    }

    @Override
    public ServiceRequestState completeService(ServiceRequest request) {
        throw new IllegalStateException("Cannot complete service in rejected state: request in rejected state cannot be changed");
    }

    @Override
    public void createReport(ServiceRequest request, Report report) {
        throw new IllegalStateException("Cannot create report in rejected state: request in rejected state cannot be changed");
    }

    @Override
    public String getStateName() {
        return "REJECTED";
    }
}