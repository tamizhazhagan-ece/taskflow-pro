package com.taskflow.controller;

import com.taskflow.dto.ActivityItemDTO;
import com.taskflow.dto.DashboardReportDTO;
import com.taskflow.dto.ProjectReportDTO;
import com.taskflow.dto.TaskDTO;
import com.taskflow.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public DashboardReportDTO dashboard() {
        return reportService.getDashboard();
    }

    @GetMapping("/projects/{id}")
    public ProjectReportDTO projectReport(@PathVariable Long id) {
        return reportService.getProjectReport(id);
    }

    @GetMapping("/tasks/overdue")
    public List<TaskDTO> overdueTasks() {
        return reportService.getOverdueTasks();
    }

    @GetMapping("/activity")
    public List<ActivityItemDTO> activity() {
        return reportService.getActivity();
    }
}
