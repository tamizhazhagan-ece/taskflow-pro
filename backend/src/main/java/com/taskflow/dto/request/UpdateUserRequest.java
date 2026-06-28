package com.taskflow.dto.request;

import com.taskflow.entity.Role;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private Role role;
    private String department;
    private String avatarColor;
    private Boolean active;
}
