package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/report")
public class AdminReportController {

    private final ReportService reportService;

    @Autowired
    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }
}