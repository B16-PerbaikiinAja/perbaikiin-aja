package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Observer for state changes in service requests.
 */
public interface StateChangeObserver extends ServiceRequestObserver {
    /**
     * Called when a service request's state changes.
     * @param request The service request that changed state
     * @param previousState The name of the previous state
     * @param newState The name of the new state
     */
    void onStateChange(ServiceRequest request, String previousState, String newState);
}