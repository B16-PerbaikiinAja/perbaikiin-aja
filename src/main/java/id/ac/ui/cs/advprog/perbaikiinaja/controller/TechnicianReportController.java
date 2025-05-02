package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/technician/report")
public class TechnicianReportController {

    private final ServiceRequestService serviceRequestService;

    @Autowired
    public TechnicianReportController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }
}