package com.taskflow.service;

import com.taskflow.dto.ActivityItemDTO;
import com.taskflow.dto.DashboardReportDTO;
import com.taskflow.dto.MemberWorkloadDTO;
import com.taskflow.dto.ProjectReportDTO;
import com.taskflow.dto.TaskDTO;
import com.taskflow.entity.Comment;
import com.taskflow.entity.Priority;
import com.taskflow.entity.Project;
import com.taskflow.entity.Role;
import com.taskflow.entity.Task;
import com.taskflow.entity.TaskStatus;
import com.taskflow.entity.User;
import com.taskflow.repository.CommentRepository;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;
    private final ProjectService projectService;
    private final SecurityUtils securityUtils;

    public ReportService(TaskRepository taskRepository, ProjectRepository projectRepository,
                         CommentRepository commentRepository, ProjectService projectService,
                         SecurityUtils securityUtils) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
        this.projectService = projectService;
        this.securityUtils = securityUtils;
    }

    public DashboardReportDTO getDashboard() {
        User current = securityUtils.getCurrentUser();
        DashboardReportDTO report = new DashboardReportDTO();

        List<Project> projects;
        if (current.getRole() == Role.ADMIN) {
            projects = projectRepository.findAllWithMembers();
        } else {
            projects = projectRepository.findByMemberOrOwner(current.getId());
        }

        report.setTotalProjects(projects.size());

        long totalTasks = 0;
        long doneTasks = 0;
        Map<TaskStatus, Long> byStatus = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            byStatus.put(status, 0L);
        }

        for (Project project : projects) {
            totalTasks += taskRepository.countByProjectId(project.getId());
            doneTasks += taskRepository.countByProjectIdAndStatus(project.getId(), TaskStatus.DONE);
            for (Object[] row : taskRepository.countByStatusForProject(project.getId())) {
                TaskStatus status = (TaskStatus) row[0];
                Long count = (Long) row[1];
                byStatus.merge(status, count, Long::sum);
            }
        }

        report.setTotalTasks(totalTasks);
        report.setTasksByStatus(byStatus);
        report.setCompletionRate(totalTasks == 0 ? 0 : (double) doneTasks / totalTasks * 100);

        List<Task> overdue = taskRepository.findOverdueTasksForUser(current.getId(), LocalDate.now());
        report.setOverdueCount(overdue.size());
        report.setOverdueTasks(overdue.stream().map(TaskDTO::from).collect(Collectors.toList()));

        return report;
    }

    public ProjectReportDTO getProjectReport(Long projectId) {
        Project project = projectService.findWithMembers(projectId);
        securityUtils.requireProjectAccess(project, securityUtils.getCurrentUser());

        ProjectReportDTO report = new ProjectReportDTO();
        report.setProjectId(project.getId());
        report.setProjectName(project.getName());

        long total = taskRepository.countByProjectId(projectId);
        long done = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.DONE);
        report.setTotalTasks(total);
        report.setDoneCount(done);
        report.setCompletionRate(total == 0 ? 0 : (double) done / total * 100);

        Map<TaskStatus, Long> byStatus = new EnumMap<>(TaskStatus.class);
        for (Object[] row : taskRepository.countByStatusForProject(projectId)) {
            byStatus.put((TaskStatus) row[0], (Long) row[1]);
        }
        report.setTasksByStatus(byStatus);

        Map<Priority, Long> byPriority = new EnumMap<>(Priority.class);
        for (Object[] row : taskRepository.countByPriorityForProject(projectId)) {
            byPriority.put((Priority) row[0], (Long) row[1]);
        }
        report.setTasksByPriority(byPriority);

        List<MemberWorkloadDTO> workload = new ArrayList<>();
        for (Object[] row : taskRepository.countByAssigneeForProject(projectId)) {
            workload.add(new MemberWorkloadDTO((Long) row[0], (String) row[1], (Long) row[2]));
        }
        report.setMemberWorkload(workload);

        return report;
    }

    public List<TaskDTO> getOverdueTasks() {
        User current = securityUtils.getCurrentUser();
        return taskRepository.findOverdueTasksForUser(current.getId(), LocalDate.now())
                .stream().map(TaskDTO::from).collect(Collectors.toList());
    }

    public List<ActivityItemDTO> getActivity() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<ActivityItemDTO> items = new ArrayList<>();

        for (Comment comment : commentRepository.findByCreatedAtAfterOrderByCreatedAtDesc(since)) {
            items.add(new ActivityItemDTO("COMMENT",
                    comment.getAuthor().getName() + " commented on " + comment.getTask().getTitle(),
                    comment.getTask().getId(),
                    comment.getCreatedAt()));
        }

        return items.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(20)
                .collect(Collectors.toList());
    }
}
