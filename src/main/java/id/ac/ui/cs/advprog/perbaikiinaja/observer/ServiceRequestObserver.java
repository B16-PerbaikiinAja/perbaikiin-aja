package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Observer interface for service request state changes.
 * Follows the Observer design pattern.
 */
public interface ServiceRequestObserver {

    /**
     * Called when a service request's state changes.
     * @param request The service request that changed state
     * @param previousState The name of the previous state
     * @param newState The name of the new state
     */
    void onStateChange(ServiceRequest request, String previousState, String newState);

    /**
     * Called when an estimate is provided for a service request.
     * @param request The service request that received an estimate
     */
    void onEstimateProvided(ServiceRequest request);

    /**
     * Called when an estimate is accepted by a customer.
     * @param request The service request with the accepted estimate
     */
    void onEstimateAccepted(ServiceRequest request);

    /**
     * Called when an estimate is rejected by a customer.
     * @param request The service request with the rejected estimate
     */
    void onEstimateRejected(ServiceRequest request);

    /**
     * Called when a service is completed.
     * @param request The completed service request
     */
    void onServiceCompleted(ServiceRequest request);

    /**
     * Called when a report is created for a service request.
     * @param request The service request that received a report
     */
    void onReportCreated(ServiceRequest request);
}