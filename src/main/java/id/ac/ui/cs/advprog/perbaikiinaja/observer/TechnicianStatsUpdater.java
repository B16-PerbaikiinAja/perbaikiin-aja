package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Observer that updates technician statistics when a service is completed.
 * Implements the Observer design pattern.
 */
public class TechnicianStatsUpdater implements ServiceCompletionObserver {

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
}