package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Observer for estimate-related events in service requests.
 */
public interface EstimateObserver extends ServiceRequestObserver {
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
}