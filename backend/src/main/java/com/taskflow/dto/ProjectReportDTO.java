package com.taskflow.dto;

import com.taskflow.entity.Priority;
import com.taskflow.entity.TaskStatus;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ProjectReportDTO {
    private Long projectId;
    private String projectName;
    private long totalTasks;
    private long doneCount;
    private double completionRate;
    private Map<TaskStatus, Long> tasksByStatus = new HashMap<>();
    private Map<Priority, Long> tasksByPriority = new HashMap<>();
    private List<MemberWorkloadDTO> memberWorkload;
}
