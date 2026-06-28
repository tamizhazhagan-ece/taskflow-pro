package com.taskflow.dto.request;

import com.taskflow.entity.Priority;
import com.taskflow.entity.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private Integer position;
    private Long assigneeId;
    private Boolean clearAssignee;
}
