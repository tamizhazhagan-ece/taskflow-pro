package com.taskflow.controller;

import com.taskflow.dto.UserDTO;
import com.taskflow.dto.request.CreateUserRequest;
import com.taskflow.dto.request.ResetPasswordRequest;
import com.taskflow.dto.request.UpdateUserRequest;
import com.taskflow.service.ProjectService;
import com.taskflow.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ProjectService projectService;
    private final UserService userService;

    public UserController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    /** All users for project member picker - authenticated users */
    @GetMapping
    public List<UserDTO> list() {
        return projectService.listUsers();
    }

    /** Admin panel: full list with filters */
    @GetMapping("/admin")
    public List<UserDTO> adminList(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String search) {
        return userService.listAll(role, department, search);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/admin")
    public UserDTO create(@RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id,
                                                              @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(id, request);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PostMapping("/{id}/activate")
    public UserDTO activate(@PathVariable Long id) {
        return userService.toggleActive(id, true);
    }

    @PostMapping("/{id}/deactivate")
    public UserDTO deactivate(@PathVariable Long id) {
        return userService.toggleActive(id, false);
    }
}
