package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.service.EstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/estimates")
public class CustomerEstimateController {

    private final EstimateService estimateService;

    @Autowired
    public CustomerEstimateController(EstimateService estimateService) {
        this.estimateService = estimateService;
    }
}