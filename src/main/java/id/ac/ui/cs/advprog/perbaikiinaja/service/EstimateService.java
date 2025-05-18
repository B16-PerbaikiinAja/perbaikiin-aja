package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.RepairEstimate;
import id.ac.ui.cs.advprog.perbaikiinaja.model.ServiceRequest;

import java.util.Optional;
import java.util.UUID;

public interface EstimateService {

    Optional<RepairEstimate> findById(UUID estimateId);

    ServiceRequest getServiceRequest(RepairEstimate estimate);

    ServiceRequest acceptEstimate(UUID estimateId, UUID customerId, String feedback);

    UUID rejectEstimate(UUID estimateId, UUID customerId, String feedback);
}