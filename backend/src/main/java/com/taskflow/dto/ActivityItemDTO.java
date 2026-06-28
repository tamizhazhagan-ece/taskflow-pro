package com.taskflow.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityItemDTO {
    private String type;
    private String message;
    private Long referenceId;
    private LocalDateTime timestamp;

    public ActivityItemDTO(String type, String message, Long referenceId, LocalDateTime timestamp) {
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
        this.timestamp = timestamp;
    }
}
