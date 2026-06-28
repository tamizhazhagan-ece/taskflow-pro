package com.taskflow.dto;

import com.taskflow.entity.Project;
import com.taskflow.entity.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private String color;
    private LocalDateTime createdAt;
    private UserDTO owner;
    private List<UserDTO> members;
    private long taskCount;
    private long doneCount;

    public static ProjectDTO from(Project project) {
        return from(project, 0, 0);
    }

    public static ProjectDTO from(Project project, long taskCount, long doneCount) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setColor(project.getColor());
        dto.setCreatedAt(project.getCreatedAt());
        if (project.getOwner() != null) {
            dto.setOwner(UserDTO.from(project.getOwner()));
        }
        dto.setMembers(project.getMembers().stream().map(UserDTO::from).collect(Collectors.toList()));
        dto.setTaskCount(taskCount);
        dto.setDoneCount(doneCount);
        return dto;
    }
}
