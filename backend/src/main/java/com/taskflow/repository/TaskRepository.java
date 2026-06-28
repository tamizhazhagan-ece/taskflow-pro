package com.taskflow.repository;

import com.taskflow.entity.Task;
import com.taskflow.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectIdOrderByPositionAsc(Long projectId);

    List<Task> findByProjectIdAndStatusOrderByPositionAsc(Long projectId, TaskStatus status);

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status <> com.taskflow.entity.TaskStatus.DONE")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.dueDate = :date AND t.status <> com.taskflow.entity.TaskStatus.DONE")
    List<Task> findTasksDueOn(@Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE (t.assignee.id = :userId OR t.reporter.id = :userId) AND t.dueDate < :today AND t.status <> com.taskflow.entity.TaskStatus.DONE")
    List<Task> findOverdueTasksForUser(@Param("userId") Long userId, @Param("today") LocalDate today);

    long countByStatus(TaskStatus status);

    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.project.id = :projectId GROUP BY t.status")
    List<Object[]> countByStatusForProject(@Param("projectId") Long projectId);

    @Query("SELECT t.priority, COUNT(t) FROM Task t WHERE t.project.id = :projectId GROUP BY t.priority")
    List<Object[]> countByPriorityForProject(@Param("projectId") Long projectId);

    @Query("SELECT t.assignee.id, t.assignee.name, COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.assignee IS NOT NULL GROUP BY t.assignee.id, t.assignee.name")
    List<Object[]> countByAssigneeForProject(@Param("projectId") Long projectId);
}
