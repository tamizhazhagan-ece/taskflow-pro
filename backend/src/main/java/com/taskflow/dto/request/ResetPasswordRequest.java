package com.taskflow.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}
