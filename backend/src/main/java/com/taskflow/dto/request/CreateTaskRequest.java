package com.taskflow.dto.request;

import com.taskflow.entity.Priority;
import com.taskflow.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    @NotBlank
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private Long assigneeId;
}
