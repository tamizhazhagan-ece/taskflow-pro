package com.taskflow.controller;

import com.taskflow.dto.TaskDTO;
import com.taskflow.dto.request.CreateTaskRequest;
import com.taskflow.dto.request.MoveTaskRequest;
import com.taskflow.dto.request.UpdateTaskRequest;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskDTO> list(@PathVariable Long projectId) {
        return taskService.listTasks(projectId);
    }

    @GetMapping("/tasks/{id}")
    public TaskDTO get(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@PathVariable Long projectId, @Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(projectId, request);
    }

    @PutMapping("/tasks/{id}")
    public TaskDTO update(@PathVariable Long id, @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request);
    }

    @PatchMapping("/tasks/{id}/move")
    public TaskDTO move(@PathVariable Long id, @Valid @RequestBody MoveTaskRequest request) {
        return taskService.moveTask(id, request);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
