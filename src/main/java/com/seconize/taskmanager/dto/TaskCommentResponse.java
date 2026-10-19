
package com.seconize.taskmanager.dto;

import com.seconize.taskmanager.entity.TaskComment;

import java.time.LocalDateTime;

public class TaskCommentResponse {

    private Long id;
    private Long taskId;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    public TaskCommentResponse(TaskComment comment) {
        this.id = comment.getId();
        this.taskId = comment.getTask().getId();
        this.content = comment.getContent();
        this.author = comment.getAuthor();
        this.createdAt = comment.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
