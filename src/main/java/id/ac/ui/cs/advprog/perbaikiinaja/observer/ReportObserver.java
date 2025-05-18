package id.ac.ui.cs.advprog.perbaikiinaja.observer;

import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

/**
 * Observer for report creation events.
 */
public interface ReportObserver extends ServiceRequestObserver {
    /**
     * Called when a report is created for a service request.
     * @param request The service request that received a report
     */
    void onReportCreated(ServiceRequest request);
}