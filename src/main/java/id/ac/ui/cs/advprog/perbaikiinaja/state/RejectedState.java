package id.ac.ui.cs.advprog.perbaikiinaja.state;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

/**
 * Represents the rejected state of a service request.
 * In this state, the estimate has been rejected by the customer and the request is terminated.
 * This is a terminal state.
 */
public class RejectedState implements ServiceRequestState {

    private static final String TERMINAL_STATE_MESSAGE = "request in rejected state cannot be changed";

    /**
     * Helper method to throw the standard error for any operation in rejected state
     *
     * @param action The action being attempted
     * @return
     * @throws IllegalStateException Always thrown with appropriate message
     */
    private ServiceRequestState throwTerminalStateException(String action) {
        throw new IllegalStateException(
                String.format("Cannot %s in rejected state: %s", action, TERMINAL_STATE_MESSAGE)
        );
    }

    @Override
    public ServiceRequestState provideEstimate(ServiceRequest request, RepairEstimate estimate) {
        return throwTerminalStateException("provide estimate");
    }

    @Override
    public ServiceRequestState acceptEstimate(ServiceRequest request) {
        return throwTerminalStateException("accept estimate");
    }

    @Override
    public ServiceRequestState rejectEstimate(ServiceRequest request) {
        return throwTerminalStateException("reject estimate");
    }

    @Override
    public ServiceRequestState startService(ServiceRequest request) {
        return throwTerminalStateException("start service");
    }

    @Override
    public ServiceRequestState completeService(ServiceRequest request) {
        return throwTerminalStateException("complete service");
    }

    @Override
    public void createReport(ServiceRequest request, Report report) {
        throwTerminalStateException("create report");
    }

    @Override
    public ServiceRequestStateType getStateType() {
        return ServiceRequestStateType.REJECTED;
    }
}