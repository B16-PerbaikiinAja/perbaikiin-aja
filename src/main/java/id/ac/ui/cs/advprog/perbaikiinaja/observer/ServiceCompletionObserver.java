package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Observer for service completion events.
 */
public interface ServiceCompletionObserver extends ServiceRequestObserver {
    /**
     * Called when a service is completed.
     * @param request The completed service request
     */
    void onServiceCompleted(ServiceRequest request);
}