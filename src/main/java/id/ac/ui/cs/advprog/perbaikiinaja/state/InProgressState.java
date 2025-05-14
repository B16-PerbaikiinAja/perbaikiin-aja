package id.ac.ui.cs.advprog.perbaikiinaja.state;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

/**
 * Represents the in-progress state of a service request.
 * In this state, the service has been started by the technician but not yet completed.
 */
public class InProgressState implements ServiceRequestState {

    @Override
    public ServiceRequestState provideEstimate(ServiceRequest request, RepairEstimate estimate) {
        throw new IllegalStateException("Cannot provide estimate in in-progress state: service is already in progress");
    }

    @Override
    public ServiceRequestState acceptEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot accept estimate in in-progress state: service is already in progress");
    }

    @Override
    public ServiceRequestState rejectEstimate(ServiceRequest request) {
        throw new IllegalStateException("Cannot reject estimate in in-progress state: service is already in progress");
    }

    @Override
    public ServiceRequestState startService(ServiceRequest request) {
        throw new IllegalStateException("Cannot start service in in-progress state: service is already in progress");
    }

    @Override
    public ServiceRequestState completeService(ServiceRequest request) {
        // Ensure technician is assigned
        if (request.getTechnician() == null) {
            throw new IllegalStateException("Cannot complete service: no technician assigned");
        }

        // Update technician statistics
        request.getTechnician().incrementCompletedJobCount();
        if (request.getEstimate() != null) {
            request.getTechnician().addEarnings(request.getEstimate().getCost());
        }

        return new CompletedState();
    }

    @Override
    public void createReport(ServiceRequest request, Report report) {
        throw new IllegalStateException("Cannot create report in in-progress state: service must be completed first");
    }

    @Override
    public ServiceRequestStateType getStateType() {
        return ServiceRequestStateType.IN_PROGRESS;
    }
}