package com.taskflow.dto;

import com.taskflow.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserDTO author;

    public static CommentDTO from(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setAuthor(UserDTO.from(comment.getAuthor()));
        return dto;
    }
}
