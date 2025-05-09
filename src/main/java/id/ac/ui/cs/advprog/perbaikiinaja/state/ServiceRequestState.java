package id.ac.ui.cs.advprog.perbaikiinaja.state;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;
import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

/**
 * Interface representing a state in the lifecycle of a service request.
 * Implements the State design pattern for managing the different states
 * a service request can be in.
 */
public interface ServiceRequestState {

    /**
     * Provides an estimate for the service request.
     * @param request The service request
     * @param estimate The repair estimate
     * @return The next state
     * @throws IllegalStateException if this action is not allowed in the current state
     */
    ServiceRequestState provideEstimate(ServiceRequest request, RepairEstimate estimate) throws IllegalStateException;

    /**
     * Accepts the estimate for the service request.
     * @param request The service request
     * @return The next state
     * @throws IllegalStateException if this action is not allowed in the current state
     */
    ServiceRequestState acceptEstimate(ServiceRequest request) throws IllegalStateException;

    /**
     * Rejects the estimate for the service request.
     * @param request The service request
     * @return The next state
     * @throws IllegalStateException if this action is not allowed in the current state
     */
    ServiceRequestState rejectEstimate(ServiceRequest request) throws IllegalStateException;

    /**
     * Starts the service for the request.
     * @param request The service request
     * @return The next state
     * @throws IllegalStateException if this action is not allowed in the current state
     */
    ServiceRequestState startService(ServiceRequest request) throws IllegalStateException;

    /**
     * Completes the service for the request.
     * @param request The service request
     * @return The next state
     * @throws IllegalStateException if this action is not allowed in the current state
     */
    ServiceRequestState completeService(ServiceRequest request) throws IllegalStateException;

    /**
     * Creates a report for the completed service request.
     * @param request The service request
     * @param report The service report
     * @throws IllegalStateException if this action is not allowed in the current state
     */
    void createReport(ServiceRequest request, Report report) throws IllegalStateException;

    /**
     * Gets the name of the current state.
     * @return The state name
     */
    String getStateName();
}