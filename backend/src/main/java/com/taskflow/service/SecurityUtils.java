package com.taskflow.service;

import com.taskflow.entity.Project;
import com.taskflow.entity.Role;
import com.taskflow.entity.User;
import com.taskflow.exception.AccessDeniedException;
import com.taskflow.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    public boolean isManagerOrAbove(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER;
    }

    public boolean isTeamLeadOrAbove(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER || user.getRole() == Role.TEAM_LEAD;
    }

    public void requireProjectAccess(Project project, User user) {
        if (isAdmin(user)) return;
        boolean isOwner = project.getOwner() != null && project.getOwner().getId().equals(user.getId());
        boolean isMember = project.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()));
        if (!isOwner && !isMember) {
            throw new AccessDeniedException("You do not have access to this project");
        }
    }

    public void requireProjectManage(Project project, User user) {
        if (isAdmin(user)) return;
        if (isManagerOrAbove(user)) {
            boolean isOwner = project.getOwner() != null && project.getOwner().getId().equals(user.getId());
            boolean isMember = project.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()));
            if (isOwner || isMember) return;
        }
        throw new AccessDeniedException("You do not have permission to manage this project");
    }

    public User findUserOrThrow(com.taskflow.repository.UserRepository repo, Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
    }
}
