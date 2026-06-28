package com.taskflow.dto;

import com.taskflow.entity.Attachment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttachmentDTO {
    private Long id;
    private String originalFileName;
    private String contentType;
    private Long size;
    private LocalDateTime createdAt;
    private UserDTO uploadedBy;

    public static AttachmentDTO from(Attachment attachment) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(attachment.getId());
        dto.setOriginalFileName(attachment.getOriginalFileName());
        dto.setContentType(attachment.getContentType());
        dto.setSize(attachment.getSize());
        dto.setCreatedAt(attachment.getCreatedAt());
        dto.setUploadedBy(UserDTO.from(attachment.getUploadedBy()));
        return dto;
    }
}
