package com.taskflow.service;

import com.taskflow.dto.NotificationDTO;
import com.taskflow.entity.Notification;
import com.taskflow.entity.NotificationType;
import com.taskflow.entity.Task;
import com.taskflow.entity.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.NotificationRepository;
import com.taskflow.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final TaskRepository taskRepository;
    private final SecurityUtils securityUtils;

    public NotificationService(NotificationRepository notificationRepository,
                               TaskRepository taskRepository, SecurityUtils securityUtils) {
        this.notificationRepository = notificationRepository;
        this.taskRepository = taskRepository;
        this.securityUtils = securityUtils;
    }

    public void notify(User user, NotificationType type, String message, Long referenceId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(message);
        notification.setReferenceId(referenceId);
        notificationRepository.save(notification);
    }

    public List<NotificationDTO> listNotifications() {
        User current = securityUtils.getCurrentUser();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(current.getId())
                .stream().map(NotificationDTO::from).collect(Collectors.toList());
    }

    public long unreadCount() {
        User current = securityUtils.getCurrentUser();
        return notificationRepository.countByUserIdAndReadFalse(current.getId());
    }

    public NotificationDTO markRead(Long id) {
        Notification notification = findOwned(id);
        notification.setRead(true);
        return NotificationDTO.from(notificationRepository.save(notification));
    }

    public void markAllRead() {
        User current = securityUtils.getCurrentUser();
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(current.getId());
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void sendDeadlineReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Task> tasks = taskRepository.findTasksDueOn(tomorrow);
        for (Task task : tasks) {
            if (task.getAssignee() != null) {
                notify(task.getAssignee(), NotificationType.DEADLINE_REMINDER,
                        "Task due tomorrow: " + task.getTitle(), task.getId());
            }
        }
    }

    private Notification findOwned(Long id) {
        User current = securityUtils.getCurrentUser();
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification " + id + " not found"));
        if (!notification.getUser().getId().equals(current.getId())) {
            throw new com.taskflow.exception.AccessDeniedException("Not your notification");
        }
        return notification;
    }
}
