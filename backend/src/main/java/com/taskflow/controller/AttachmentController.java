package com.taskflow.controller;

import com.taskflow.dto.AttachmentDTO;
import com.taskflow.service.AttachmentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping("/tasks/{taskId}/attachments")
    public List<AttachmentDTO> list(@PathVariable Long taskId) {
        return attachmentService.listAttachments(taskId);
    }

    @PostMapping("/tasks/{taskId}/attachments")
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentDTO upload(@PathVariable Long taskId, @RequestParam("file") MultipartFile file) {
        return attachmentService.upload(taskId, file);
    }

    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Resource resource = attachmentService.download(id);
        String filename = attachmentService.getOriginalFileName(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @DeleteMapping("/attachments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        attachmentService.delete(id);
    }
}
