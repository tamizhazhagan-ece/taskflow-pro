package com.taskflow.dto;

import com.taskflow.entity.TaskStatus;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DashboardReportDTO {
    private long totalProjects;
    private long totalTasks;
    private long overdueCount;
    private double completionRate;
    private Map<TaskStatus, Long> tasksByStatus = new HashMap<>();
    private List<TaskDTO> overdueTasks;
}
