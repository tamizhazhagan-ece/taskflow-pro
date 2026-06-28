package com.taskflow.service;

import com.taskflow.dto.AttachmentDTO;
import com.taskflow.entity.Attachment;
import com.taskflow.entity.Role;
import com.taskflow.entity.Task;
import com.taskflow.entity.User;
import com.taskflow.exception.AccessDeniedException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.AttachmentRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskService taskService;
    private final FileStorageService fileStorageService;
    private final SecurityUtils securityUtils;

    public AttachmentService(AttachmentRepository attachmentRepository, TaskService taskService,
                             FileStorageService fileStorageService, SecurityUtils securityUtils) {
        this.attachmentRepository = attachmentRepository;
        this.taskService = taskService;
        this.fileStorageService = fileStorageService;
        this.securityUtils = securityUtils;
    }

    public List<AttachmentDTO> listAttachments(Long taskId) {
        Task task = taskService.find(taskId);
        securityUtils.requireProjectAccess(task.getProject(), securityUtils.getCurrentUser());
        return attachmentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream().map(AttachmentDTO::from).collect(Collectors.toList());
    }

    public AttachmentDTO upload(Long taskId, MultipartFile file) {
        Task task = taskService.find(taskId);
        User current = securityUtils.getCurrentUser();
        securityUtils.requireProjectAccess(task.getProject(), current);

        String storedName = fileStorageService.store(file);

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setUploadedBy(current);
        attachment.setStoredFileName(storedName);
        attachment.setOriginalFileName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());

        return AttachmentDTO.from(attachmentRepository.save(attachment));
    }

    public Resource download(Long id) {
        Attachment attachment = find(id);
        Task task = attachment.getTask();
        securityUtils.requireProjectAccess(task.getProject(), securityUtils.getCurrentUser());

        try {
            Path path = fileStorageService.load(attachment.getStoredFileName());
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) {
                throw new ResourceNotFoundException("File not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File not found");
        }
    }

    public String getOriginalFileName(Long id) {
        return find(id).getOriginalFileName();
    }

    public void delete(Long id) {
        Attachment attachment = find(id);
        User current = securityUtils.getCurrentUser();
        if (!attachment.getUploadedBy().getId().equals(current.getId()) && current.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only delete your own attachments");
        }
        fileStorageService.delete(attachment.getStoredFileName());
        attachmentRepository.delete(attachment);
    }

    private Attachment find(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment " + id + " not found"));
    }
}
