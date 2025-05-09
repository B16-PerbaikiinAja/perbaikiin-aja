package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Observer that updates technician statistics when a service is completed.
 * Implements the Observer design pattern.
 */
public class TechnicianStatsUpdater implements ServiceRequestObserver {

    // The InProgressState already updates technician stats when transitioning to CompletedState,
    // but this observer provides a more explicit way to handle it and could be extended
    // to include additional statistics tracking or persistence.

    @Override
    public void onStateChange(ServiceRequest request, String previousState, String newState) {
        // Not needed for this implementation
    }

    @Override
    public void onEstimateProvided(ServiceRequest request) {
        // Not needed for this implementation
    }

    @Override
    public void onEstimateAccepted(ServiceRequest request) {
        // Not needed for this implementation
    }

    @Override
    public void onEstimateRejected(ServiceRequest request) {
        // Not needed for this implementation
    }

    @Override
    public void onServiceCompleted(ServiceRequest request) {
        if (request.getTechnician() != null && request.getEstimate() != null) {
            // Update technician statistics
            request.getTechnician().incrementCompletedJobCount();
            request.getTechnician().addEarnings(request.getEstimate().getCost());

            System.out.println("Updated statistics for technician: " + request.getTechnician().getFullName() +
                    ". Total completed jobs: " + request.getTechnician().getCompletedJobCount() +
                    ", Total earnings: " + request.getTechnician().getTotalEarnings());
        }
    }

    @Override
    public void onReportCreated(ServiceRequest request) {
        // Not needed for this implementation
    }
}