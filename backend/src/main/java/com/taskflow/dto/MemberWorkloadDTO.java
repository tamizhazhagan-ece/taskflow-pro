package com.taskflow.dto;

import lombok.Data;

@Data
public class MemberWorkloadDTO {
    private Long userId;
    private String userName;
    private long taskCount;

    public MemberWorkloadDTO(Long userId, String userName, long taskCount) {
        this.userId = userId;
        this.userName = userName;
        this.taskCount = taskCount;
    }
}
