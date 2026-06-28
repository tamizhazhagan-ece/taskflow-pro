package com.taskflow.dto;

import com.taskflow.entity.Priority;
import com.taskflow.entity.Task;
import com.taskflow.entity.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
    private UserDTO assignee;
    private UserDTO reporter;

    public static TaskDTO from(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setPosition(task.getPosition());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setProjectId(task.getProject().getId());
        if (task.getAssignee() != null) {
            dto.setAssignee(UserDTO.from(task.getAssignee()));
        }
        if (task.getReporter() != null) {
            dto.setReporter(UserDTO.from(task.getReporter()));
        }
        return dto;
    }
}
