package id.ac.ui.cs.advprog.perbaikiinaja.service;

import id.ac.ui.cs.advprog.perbaikiinaja.model.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportService {

    List<Report> getAllReports();

    Report getReportById(UUID reportId);

    List<Report> getReportsByTechnician(UUID technicianId);

    List<Report> getReportsByDateRange(LocalDate startDate, LocalDate endDate);
}