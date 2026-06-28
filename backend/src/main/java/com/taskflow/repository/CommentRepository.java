package com.taskflow.repository;

import com.taskflow.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    List<Comment> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime since);
}
