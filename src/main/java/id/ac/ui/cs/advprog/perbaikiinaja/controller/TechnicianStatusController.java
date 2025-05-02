package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/technician/service-requests")
public class TechnicianStatusController {

    private final ServiceRequestService serviceRequestService;
    private final Set<String> validStatusValues = new HashSet<>(Arrays.asList(
            "IN_PROGRESS", "COMPLETED"
    ));

    @Autowired
    public TechnicianStatusController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }
}