package com.taskflow.dto;

import com.taskflow.entity.Role;
import com.taskflow.entity.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String avatarColor;
    private String department;
    private boolean active;

    public static UserDTO from(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setAvatarColor(user.getAvatarColor());
        dto.setDepartment(user.getDepartment());
        dto.setActive(user.isActive());
        return dto;
    }
}
