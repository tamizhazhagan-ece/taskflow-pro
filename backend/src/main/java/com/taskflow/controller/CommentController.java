package com.taskflow.controller;

import com.taskflow.dto.CommentDTO;
import com.taskflow.dto.request.CreateCommentRequest;
import com.taskflow.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/tasks/{taskId}/comments")
    public List<CommentDTO> list(@PathVariable Long taskId) {
        return commentService.listComments(taskId);
    }

    @PostMapping("/tasks/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO create(@PathVariable Long taskId, @Valid @RequestBody CreateCommentRequest request) {
        return commentService.addComment(taskId, request);
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
