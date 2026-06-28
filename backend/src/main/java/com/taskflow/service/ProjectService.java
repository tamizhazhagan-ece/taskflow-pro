package com.taskflow.service;

import com.taskflow.dto.ProjectDTO;
import com.taskflow.dto.UserDTO;
import com.taskflow.dto.request.CreateProjectRequest;
import com.taskflow.dto.request.MemberRequest;
import com.taskflow.dto.request.UpdateProjectRequest;
import com.taskflow.entity.NotificationType;
import com.taskflow.entity.Project;
import com.taskflow.entity.Role;
import com.taskflow.entity.TaskStatus;
import com.taskflow.entity.User;
import com.taskflow.exception.AccessDeniedException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final NotificationService notificationService;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository,
                          UserRepository userRepository, SecurityUtils securityUtils,
                          NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.notificationService = notificationService;
    }

    public List<ProjectDTO> listProjects() {
        User current = securityUtils.getCurrentUser();
        List<Project> projects;
        if (current.getRole() == Role.ADMIN) {
            projects = projectRepository.findAllWithMembers();
        } else {
            projects = projectRepository.findByMemberOrOwner(current.getId());
        }
        return projects.stream().map(this::toDtoWithCounts).collect(Collectors.toList());
    }

    public List<UserDTO> listUsers() {
        return userRepository.findAll().stream().map(UserDTO::from).collect(Collectors.toList());
    }

    public ProjectDTO getProject(Long id) {
        Project project = findWithMembers(id);
        securityUtils.requireProjectAccess(project, securityUtils.getCurrentUser());
        return toDtoWithCounts(project);
    }

    public ProjectDTO createProject(CreateProjectRequest request) {
        User current = securityUtils.getCurrentUser();
        if (current.getRole() == Role.DEVELOPER || current.getRole() == Role.TEAM_LEAD) {
            throw new AccessDeniedException("Only Managers and Admins can create projects");
        }

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        if (request.getColor() != null) {
            project.setColor(request.getColor());
        }
        project.setOwner(current);
        project.getMembers().add(current);

        return toDtoWithCounts(projectRepository.save(project));
    }

    public ProjectDTO updateProject(Long id, UpdateProjectRequest request) {
        Project project = findWithMembers(id);
        securityUtils.requireProjectManage(project, securityUtils.getCurrentUser());

        if (request.getName() != null) project.setName(request.getName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getColor() != null) project.setColor(request.getColor());

        return toDtoWithCounts(projectRepository.save(project));
    }

    public void deleteProject(Long id) {
        Project project = findWithMembers(id);
        User current = securityUtils.getCurrentUser();
        if (current.getRole() != Role.ADMIN && !project.getOwner().getId().equals(current.getId())) {
            throw new AccessDeniedException("Only the project owner or admin can delete this project");
        }
        projectRepository.delete(project);
    }

    public ProjectDTO addMember(Long projectId, MemberRequest request) {
        Project project = findWithMembers(projectId);
        securityUtils.requireProjectManage(project, securityUtils.getCurrentUser());

        User member = securityUtils.findUserOrThrow(userRepository, request.getUserId());
        project.getMembers().add(member);
        Project saved = projectRepository.save(project);

        notificationService.notify(member, NotificationType.PROJECT_INVITE,
                "You were added to project: " + project.getName(), project.getId());

        return toDtoWithCounts(saved);
    }

    public ProjectDTO removeMember(Long projectId, Long userId) {
        Project project = findWithMembers(projectId);
        securityUtils.requireProjectManage(project, securityUtils.getCurrentUser());

        project.getMembers().removeIf(m -> m.getId().equals(userId));
        return toDtoWithCounts(projectRepository.save(project));
    }

    public Project findWithMembers(Long id) {
        return projectRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project " + id + " not found"));
    }

    private ProjectDTO toDtoWithCounts(Project project) {
        long total = taskRepository.countByProjectId(project.getId());
        long done = taskRepository.countByProjectIdAndStatus(project.getId(), TaskStatus.DONE);
        return ProjectDTO.from(project, total, done);
    }
}
