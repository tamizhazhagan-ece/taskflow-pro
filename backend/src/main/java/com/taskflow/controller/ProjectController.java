package com.taskflow.controller;

import com.taskflow.dto.ProjectDTO;
import com.taskflow.dto.UserDTO;
import com.taskflow.dto.request.CreateProjectRequest;
import com.taskflow.dto.request.MemberRequest;
import com.taskflow.dto.request.UpdateProjectRequest;
import com.taskflow.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectDTO> list() {
        return projectService.listProjects();
    }

    @GetMapping("/{id}")
    public ProjectDTO get(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDTO create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    @PutMapping("/{id}")
    public ProjectDTO update(@PathVariable Long id, @RequestBody UpdateProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @PostMapping("/{id}/members")
    public ProjectDTO addMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return projectService.addMember(id, request);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ProjectDTO removeMember(@PathVariable Long id, @PathVariable Long userId) {
        return projectService.removeMember(id, userId);
    }
}
