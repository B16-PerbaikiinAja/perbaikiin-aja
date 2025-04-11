package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Observer that notifies customers of service request state changes.
 * Implements the Observer design pattern.
 */
public class CustomerNotifier implements ServiceRequestObserver {

    // This could be injected with a NotificationService in a real application

    @Override
    public void onStateChange(ServiceRequest request, String previousState, String newState) {
        // Generic state change notification - could be implemented for specific state transitions
        System.out.println("Notifying customer: " + request.getCustomer().getFullName() +
                " that their service request #" + request.getId() +
                " has changed from " + previousState + " to " + newState);
    }

    @Override
    public void onEstimateProvided(ServiceRequest request) {
        System.out.println("Notifying customer: " + request.getCustomer().getFullName() +
                " that an estimate has been provided for their service request #" + request.getId() +
                ". Cost: " + request.getEstimate().getCost() +
                ", Estimated completion: " + request.getEstimate().getCompletionDate());
    }

    @Override
    public void onEstimateAccepted(ServiceRequest request) {
        System.out.println("Confirming to customer: " + request.getCustomer().getFullName() +
                " that they have accepted the estimate for service request #" + request.getId());
    }

    @Override
    public void onEstimateRejected(ServiceRequest request) {
        System.out.println("Confirming to customer: " + request.getCustomer().getFullName() +
                " that they have rejected the estimate for service request #" + request.getId() +
                ". The request has been cancelled.");
    }

    @Override
    public void onServiceCompleted(ServiceRequest request) {
        System.out.println("Notifying customer: " + request.getCustomer().getFullName() +
                " that their service request #" + request.getId() +
                " has been completed by technician: " + request.getTechnician().getFullName());
    }

    @Override
    public void onReportCreated(ServiceRequest request) {
        System.out.println("Notifying customer: " + request.getCustomer().getFullName() +
                " that a report has been created for their service request #" + request.getId() +
                ". Summary: " + request.getReport().getRepairSummary());
    }
}