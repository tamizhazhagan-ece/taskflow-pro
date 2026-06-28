package com.taskflow.dto.request;

import com.taskflow.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MoveTaskRequest {
    @NotNull
    private TaskStatus status;
    private Integer position;
}
