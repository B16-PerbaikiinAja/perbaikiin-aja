package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import java.util.ArrayList;
import java.util.List;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.ServiceRequestStateType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Subject class that notifies observers of service request state changes.
 * Follows the Observer design pattern.
 */
public class ServiceRequestSubject {

    private final List<ServiceCompletionObserver> completionObservers = new ArrayList<>();

    /**
     * Registers an observer to be notified of events.
     * @param observer The observer to register
     */
    public void addObserver(ServiceRequestObserver observer) {
        if (observer == null) {
            return;
        }

        if (observer instanceof ServiceCompletionObserver && !completionObservers.contains(observer)) {
            completionObservers.add((ServiceCompletionObserver) observer);
        }
    }

    /**
     * Unregisters an observer so it no longer receives notifications.
     * @param observer The observer to unregister
     */
    public void removeObserver(ServiceRequestObserver observer) {
        if (observer instanceof ServiceCompletionObserver) {
            completionObservers.remove(observer);
        }
    }

    /**
     * Notifies all observers that a service has been completed.
     * @param request The completed service request
     */
    public void notifyServiceCompleted(ServiceRequest request) {
        for (ServiceCompletionObserver observer : completionObservers) {
            observer.onServiceCompleted(request);
        }
    }
}