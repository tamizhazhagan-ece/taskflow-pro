package com.taskflow.service;

import com.taskflow.dto.CommentDTO;
import com.taskflow.dto.request.CreateCommentRequest;
import com.taskflow.entity.Comment;
import com.taskflow.entity.NotificationType;
import com.taskflow.entity.Role;
import com.taskflow.entity.Task;
import com.taskflow.entity.User;
import com.taskflow.exception.AccessDeniedException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final SecurityUtils securityUtils;
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository, TaskService taskService,
                          SecurityUtils securityUtils, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.taskService = taskService;
        this.securityUtils = securityUtils;
        this.notificationService = notificationService;
    }

    public List<CommentDTO> listComments(Long taskId) {
        Task task = taskService.find(taskId);
        securityUtils.requireProjectAccess(task.getProject(), securityUtils.getCurrentUser());
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream().map(CommentDTO::from).collect(Collectors.toList());
    }

    public CommentDTO addComment(Long taskId, CreateCommentRequest request) {
        Task task = taskService.find(taskId);
        User current = securityUtils.getCurrentUser();
        securityUtils.requireProjectAccess(task.getProject(), current);

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setAuthor(current);
        comment.setContent(request.getContent());

        Comment saved = commentRepository.save(comment);

        Set<User> recipients = new HashSet<>();
        if (task.getAssignee() != null) recipients.add(task.getAssignee());
        if (task.getReporter() != null) recipients.add(task.getReporter());
        recipients.remove(current);

        for (User user : recipients) {
            notificationService.notify(user, NotificationType.COMMENT_ADDED,
                    current.getName() + " commented on: " + task.getTitle(), task.getId());
        }

        return CommentDTO.from(saved);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment " + id + " not found"));
        User current = securityUtils.getCurrentUser();
        if (!comment.getAuthor().getId().equals(current.getId()) && current.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }
}
