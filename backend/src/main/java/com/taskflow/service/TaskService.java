package com.taskflow.service;

import com.taskflow.dto.TaskDTO;
import com.taskflow.dto.request.CreateTaskRequest;
import com.taskflow.dto.request.MoveTaskRequest;
import com.taskflow.dto.request.UpdateTaskRequest;
import com.taskflow.entity.NotificationType;
import com.taskflow.entity.Project;
import com.taskflow.entity.Task;
import com.taskflow.entity.TaskStatus;
import com.taskflow.entity.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final NotificationService notificationService;

    public TaskService(TaskRepository taskRepository, ProjectService projectService,
                       UserRepository userRepository, SecurityUtils securityUtils,
                       NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.notificationService = notificationService;
    }

    public List<TaskDTO> listTasks(Long projectId) {
        Project project = projectService.findWithMembers(projectId);
        securityUtils.requireProjectAccess(project, securityUtils.getCurrentUser());
        return taskRepository.findByProjectIdOrderByPositionAsc(projectId)
                .stream().map(TaskDTO::from).collect(Collectors.toList());
    }

    public TaskDTO getTask(Long id) {
        Task task = find(id);
        securityUtils.requireProjectAccess(task.getProject(), securityUtils.getCurrentUser());
        return TaskDTO.from(task);
    }

    public TaskDTO createTask(Long projectId, CreateTaskRequest request) {
        Project project = projectService.findWithMembers(projectId);
        User current = securityUtils.getCurrentUser();
        securityUtils.requireProjectAccess(project, current);

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        task.setDueDate(request.getDueDate());
        task.setReporter(current);

        if (request.getAssigneeId() != null) {
            User assignee = securityUtils.findUserOrThrow(userRepository, request.getAssigneeId());
            task.setAssignee(assignee);
        }

        int nextPosition = (int) taskRepository
                .findByProjectIdAndStatusOrderByPositionAsc(projectId, task.getStatus()).size();
        task.setPosition(nextPosition);

        Task saved = taskRepository.save(task);

        if (saved.getAssignee() != null && !saved.getAssignee().getId().equals(current.getId())) {
            notificationService.notify(saved.getAssignee(), NotificationType.TASK_ASSIGNED,
                    "You were assigned to task: " + saved.getTitle(), saved.getId());
        }

        return TaskDTO.from(saved);
    }

    public TaskDTO updateTask(Long id, UpdateTaskRequest request) {
        Task task = find(id);
        securityUtils.requireProjectAccess(task.getProject(), securityUtils.getCurrentUser());

        Long oldAssigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;
        TaskStatus oldStatus = task.getStatus();

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getPosition() != null) task.setPosition(request.getPosition());

        if (Boolean.TRUE.equals(request.getClearAssignee())) {
            task.setAssignee(null);
        } else if (request.getAssigneeId() != null) {
            task.setAssignee(securityUtils.findUserOrThrow(userRepository, request.getAssigneeId()));
        }

        Task saved = taskRepository.save(task);

        if (saved.getAssignee() != null && !Objects.equals(oldAssigneeId, saved.getAssignee().getId())) {
            notificationService.notify(saved.getAssignee(), NotificationType.TASK_ASSIGNED,
                    "You were assigned to task: " + saved.getTitle(), saved.getId());
        }

        if (!oldStatus.equals(saved.getStatus()) || request.getPriority() != null || request.getDueDate() != null) {
            notifyTaskUpdated(saved);
        }

        return TaskDTO.from(saved);
    }

    public TaskDTO moveTask(Long id, MoveTaskRequest request) {
        Task task = find(id);
        securityUtils.requireProjectAccess(task.getProject(), securityUtils.getCurrentUser());

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(request.getStatus());
        if (request.getPosition() != null) {
            task.setPosition(request.getPosition());
        } else {
            task.setPosition((int) taskRepository.findByProjectIdAndStatusOrderByPositionAsc(
                    task.getProject().getId(), request.getStatus()).size());
        }

        Task saved = taskRepository.save(task);
        if (!oldStatus.equals(saved.getStatus())) {
            notifyTaskUpdated(saved);
        }
        return TaskDTO.from(saved);
    }

    public void deleteTask(Long id) {
        Task task = find(id);
        securityUtils.requireProjectAccess(task.getProject(), securityUtils.getCurrentUser());
        taskRepository.delete(task);
    }

    public Task find(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task " + id + " not found"));
    }

    private void notifyTaskUpdated(Task task) {
        Set<User> recipients = new HashSet<>();
        if (task.getAssignee() != null) recipients.add(task.getAssignee());
        if (task.getReporter() != null) recipients.add(task.getReporter());
        User current = securityUtils.getCurrentUser();
        recipients.remove(current);

        for (User user : recipients) {
            notificationService.notify(user, NotificationType.TASK_UPDATED,
                    "Task updated: " + task.getTitle(), task.getId());
        }
    }
}
