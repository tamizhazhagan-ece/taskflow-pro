package com.taskflow.service;

import com.taskflow.dto.UserDTO;
import com.taskflow.dto.request.CreateUserRequest;
import com.taskflow.dto.request.ResetPasswordRequest;
import com.taskflow.dto.request.UpdateUserRequest;
import com.taskflow.entity.Role;
import com.taskflow.entity.User;
import com.taskflow.exception.AccessDeniedException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, SecurityUtils securityUtils,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> listAll(String role, String department, String search) {
        requireAdminOrManager();
        List<User> users = userRepository.findAll();

        if (role != null && !role.isBlank()) {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            users = users.stream().filter(u -> u.getRole() == roleEnum).collect(Collectors.toList());
        }
        if (department != null && !department.isBlank()) {
            String dep = department.toLowerCase();
            users = users.stream()
                    .filter(u -> u.getDepartment() != null && u.getDepartment().toLowerCase().contains(dep))
                    .collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            users = users.stream()
                    .filter(u -> u.getName().toLowerCase().contains(q) || u.getEmail().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        return users.stream().map(UserDTO::from).collect(Collectors.toList());
    }

    public UserDTO getById(Long id) {
        requireAdminOrManager();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return UserDTO.from(user);
    }

    public UserDTO create(CreateUserRequest request) {
        User current = securityUtils.getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admins can create users");
        }

        // Only admin can create admin
        if (request.getRole() == Role.ADMIN) {
            if (current.getRole() != Role.ADMIN) {
                throw new AccessDeniedException("Only Admins can create other Admins");
            }
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.DEVELOPER);
        user.setDepartment(request.getDepartment());
        user.setAvatarColor(randomColor());
        user.setActive(true);

        return UserDTO.from(userRepository.save(user));
    }

    public UserDTO update(Long id, UpdateUserRequest request) {
        User current = securityUtils.getCurrentUser();
        requireAdminOrManager();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        // Only admin can change roles
        if (request.getRole() != null && request.getRole() != user.getRole()) {
            if (current.getRole() != Role.ADMIN) {
                throw new AccessDeniedException("Only Admins can change roles");
            }
            // Only admin can promote to admin
            if (request.getRole() == Role.ADMIN && current.getRole() != Role.ADMIN) {
                throw new AccessDeniedException("Only Admins can promote to Admin");
            }
        }

        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment());
        if (request.getAvatarColor() != null) user.setAvatarColor(request.getAvatarColor());
        if (request.getActive() != null) user.setActive(request.getActive());

        return UserDTO.from(userRepository.save(user));
    }

    public void delete(Long id) {
        User current = securityUtils.getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only Admins can delete users");
        }
        if (current.getId().equals(id)) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        // Managers cannot be deleted by non-admin
        userRepository.delete(user);
    }

    public void resetPassword(Long id, ResetPasswordRequest request) {
        User current = securityUtils.getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only Admins can reset passwords");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserDTO toggleActive(Long id, boolean active) {
        User current = securityUtils.getCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only Admins can activate/deactivate users");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setActive(active);
        return UserDTO.from(userRepository.save(user));
    }

    private void requireAdminOrManager() {
        User current = securityUtils.getCurrentUser();
        if (current.getRole() == Role.DEVELOPER || current.getRole() == Role.TEAM_LEAD) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private String randomColor() {
        String[] colors = {"#6366f1", "#8b5cf6", "#ec4899", "#f59e0b", "#10b981", "#3b82f6", "#ef4444", "#0ea5e9"};
        return colors[(int) (Math.random() * colors.length)];
    }
}
