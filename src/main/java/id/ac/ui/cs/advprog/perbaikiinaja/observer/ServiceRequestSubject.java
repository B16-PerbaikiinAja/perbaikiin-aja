package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import java.util.ArrayList;
import java.util.List;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Subject class that notifies observers of service request state changes.
 * Follows the Observer design pattern.
 */
public class ServiceRequestSubject {

    private final List<ServiceRequestObserver> observers = new ArrayList<>();

    /**
     * Registers an observer to be notified of service request state changes.
     * @param observer The observer to register
     */
    public void addObserver(ServiceRequestObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer so it no longer receives notifications.
     * @param observer The observer to unregister
     */
    public void removeObserver(ServiceRequestObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers that a service request's state has changed.
     * @param request The service request that changed state
     * @param previousState The name of the previous state
     * @param newState The name of the new state
     */
    public void notifyStateChange(ServiceRequest request, String previousState, String newState) {
        for (ServiceRequestObserver observer : observers) {
            observer.onStateChange(request, previousState, newState);
        }
    }

    /**
     * Notifies all observers that an estimate has been provided for a service request.
     * @param request The service request that received an estimate
     */
    public void notifyEstimateProvided(ServiceRequest request) {
        for (ServiceRequestObserver observer : observers) {
            observer.onEstimateProvided(request);
        }
    }

    /**
     * Notifies all observers that an estimate has been accepted by a customer.
     * @param request The service request with the accepted estimate
     */
    public void notifyEstimateAccepted(ServiceRequest request) {
        for (ServiceRequestObserver observer : observers) {
            observer.onEstimateAccepted(request);
        }
    }

    /**
     * Notifies all observers that an estimate has been rejected by a customer.
     * @param request The service request with the rejected estimate
     */
    public void notifyEstimateRejected(ServiceRequest request) {
        for (ServiceRequestObserver observer : observers) {
            observer.onEstimateRejected(request);
        }
    }

    /**
     * Notifies all observers that a service has been completed.
     * @param request The completed service request
     */
    public void notifyServiceCompleted(ServiceRequest request) {
        for (ServiceRequestObserver observer : observers) {
            observer.onServiceCompleted(request);
        }
    }

    /**
     * Notifies all observers that a report has been created for a service request.
     * @param request The service request that received a report
     */
    public void notifyReportCreated(ServiceRequest request) {
        for (ServiceRequestObserver observer : observers) {
            observer.onReportCreated(request);
        }
    }
}