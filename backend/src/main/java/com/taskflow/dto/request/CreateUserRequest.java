package com.taskflow.dto.request;

import com.taskflow.entity.Role;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
    private String department;
}
