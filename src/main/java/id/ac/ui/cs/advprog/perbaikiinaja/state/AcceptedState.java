package id.ac.ui.cs.advprog.perbaikiinaja.state;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

/**
 * Represents the accepted state of a service request.
 * In this state, the estimate has been accepted by the customer but the service has not started yet.
 */
public class AcceptedState implements ServiceRequestState {

    @Override
    public ServiceRequestState provideEstimate(ServiceRequest request, RepairEstimate estimate) {
        throw new IllegalStateException("Cannot provide estimate in accepted state: estimate has already been accepted");
    }

    @Override
    public ServiceRequestState acceptEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot accept estimate in accepted state: estimate has already been accepted");
    }

    @Override
    public ServiceRequestState rejectEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot reject estimate in accepted state: estimate has already been accepted");
    }

    @Override
    public ServiceRequestState startService(ServiceRequest request) {
        // Ensure technician is assigned
        if (request.getTechnician() == null) {
            throw new IllegalStateException("Cannot start service: no technician assigned");
        }

        return new InProgressState();
    }

    @Override
    public ServiceRequestState completeService(ServiceRequest request) {
        throw new IllegalStateException("Cannot complete service in accepted state: service must be started first");
    }

    @Override
    public void createReport(ServiceRequest request, Report report) {
        throw new IllegalStateException("Cannot create report in accepted state: service must be completed first");
    }

    @Override
    public String getStateName() {
        return "ACCEPTED";
    }
}