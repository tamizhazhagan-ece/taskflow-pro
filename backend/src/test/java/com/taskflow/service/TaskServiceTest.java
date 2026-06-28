package com.taskflow.service;

import com.taskflow.dto.request.CreateTaskRequest;
import com.taskflow.dto.request.MoveTaskRequest;
import com.taskflow.entity.*;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User manager;
    private Project project;

    @BeforeEach
    void setUp() {
        manager = new User();
        manager.setName("Manager");
        manager.setEmail("manager@test.com");
        manager.setPassword(passwordEncoder.encode("password123"));
        manager.setRole(Role.MANAGER);
        manager = userRepository.save(manager);

        project = new Project();
        project.setName("Test Project");
        project.setOwner(manager);
        project.setMembers(Set.of(manager));
        project = projectRepository.save(project);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(manager, null, manager.getAuthorities()));
    }

    @Test
    void createAndMoveTask() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setStatus(TaskStatus.TODO);

        var created = taskService.createTask(project.getId(), request);
        assertEquals("Test Task", created.getTitle());
        assertEquals(TaskStatus.TODO, created.getStatus());

        MoveTaskRequest moveRequest = new MoveTaskRequest();
        moveRequest.setStatus(TaskStatus.IN_PROGRESS);
        moveRequest.setPosition(0);
        var moved = taskService.moveTask(created.getId(), moveRequest);

        assertEquals(TaskStatus.IN_PROGRESS, moved.getStatus());

        Task saved = taskRepository.findById(created.getId()).orElseThrow();
        assertEquals(TaskStatus.IN_PROGRESS, saved.getStatus());
    }
}
