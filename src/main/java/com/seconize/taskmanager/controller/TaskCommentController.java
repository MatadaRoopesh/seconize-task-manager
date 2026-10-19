
package com.seconize.taskmanager.controller;

import com.seconize.taskmanager.dto.TaskCommentResponse;
import com.seconize.taskmanager.entity.Task;
import com.seconize.taskmanager.entity.TaskComment;
import com.seconize.taskmanager.repository.TaskCommentRepository;
import com.seconize.taskmanager.repository.TaskRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class TaskCommentController {

    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;

    public TaskCommentController(
            TaskCommentRepository commentRepository,
            TaskRepository taskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
    }

    public static class CreateCommentRequest {

        @NotBlank(message = "Comment cannot be empty")
        @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
        private String content;

        @Size(max = 100, message = "Author cannot exceed 100 characters")
        private String author = "Roopesh";

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public TaskCommentResponse createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found with ID: " + taskId));

        TaskComment comment = new TaskComment();
        comment.setTask(task);
        comment.setContent(request.getContent().trim());

        String author = request.getAuthor();
        comment.setAuthor(
                author == null || author.isBlank()
                        ? "Roopesh"
                        : author.trim());

        return new TaskCommentResponse(
                commentRepository.save(comment));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getComments(
            @PathVariable Long taskId) {

        if (!taskRepository.existsById(taskId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task not found with ID: " + taskId);
        }

        return commentRepository
                .findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(TaskCommentResponse::new)
                .toList();
    }
}
