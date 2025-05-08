package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import java.util.ArrayList;
import java.util.List;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Subject class that notifies observers of service request state changes.
 * Follows the Observer design pattern.
 */
public class ServiceRequestSubject {

    private final List<StateChangeObserver> stateObservers = new ArrayList<>();
    private final List<EstimateObserver> estimateObservers = new ArrayList<>();
    private final List<ServiceCompletionObserver> completionObservers = new ArrayList<>();
    private final List<ReportObserver> reportObservers = new ArrayList<>();

    /**
     * Registers an observer to be notified of events.
     * @param observer The observer to register
     */
    public void addObserver(ServiceRequestObserver observer) {
        if (observer == null) {
            return;
        }

        if (observer instanceof StateChangeObserver && !stateObservers.contains(observer)) {
            stateObservers.add((StateChangeObserver) observer);
        }

        if (observer instanceof EstimateObserver && !estimateObservers.contains(observer)) {
            estimateObservers.add((EstimateObserver) observer);
        }

        if (observer instanceof ServiceCompletionObserver && !completionObservers.contains(observer)) {
            completionObservers.add((ServiceCompletionObserver) observer);
        }

        if (observer instanceof ReportObserver && !reportObservers.contains(observer)) {
            reportObservers.add((ReportObserver) observer);
        }
    }

    /**
     * Unregisters an observer so it no longer receives notifications.
     * @param observer The observer to unregister
     */
    public void removeObserver(ServiceRequestObserver observer) {
        if (observer instanceof StateChangeObserver) {
            stateObservers.remove(observer);
        }

        if (observer instanceof EstimateObserver) {
            estimateObservers.remove(observer);
        }

        if (observer instanceof ServiceCompletionObserver) {
            completionObservers.remove(observer);
        }

        if (observer instanceof ReportObserver) {
            reportObservers.remove(observer);
        }
    }

    /**
     * Notifies all observers that a service request's state has changed.
     * @param request The service request that changed state
     * @param previousState The name of the previous state
     * @param newState The name of the new state
     */
    public void notifyStateChange(ServiceRequest request, String previousState, String newState) {
        for (StateChangeObserver observer : stateObservers) {
            observer.onStateChange(request, previousState, newState);
        }
    }

    /**
     * Notifies all observers that an estimate has been provided for a service request.
     * @param request The service request that received an estimate
     */
    public void notifyEstimateProvided(ServiceRequest request) {
        for (EstimateObserver observer : estimateObservers) {
            observer.onEstimateProvided(request);
        }
    }

    /**
     * Notifies all observers that an estimate has been accepted by a customer.
     * @param request The service request with the accepted estimate
     */
    public void notifyEstimateAccepted(ServiceRequest request) {
        for (EstimateObserver observer : estimateObservers) {
            observer.onEstimateAccepted(request);
        }
    }

    /**
     * Notifies all observers that an estimate has been rejected by a customer.
     * @param request The service request with the rejected estimate
     */
    public void notifyEstimateRejected(ServiceRequest request) {
        for (EstimateObserver observer : estimateObservers) {
            observer.onEstimateRejected(request);
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

    /**
     * Notifies all observers that a report has been created for a service request.
     * @param request The service request that received a report
     */
    public void notifyReportCreated(ServiceRequest request) {
        for (ReportObserver observer : reportObservers) {
            observer.onReportCreated(request);
        }
    }
}