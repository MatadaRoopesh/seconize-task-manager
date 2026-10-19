
package com.seconize.taskmanager.dto;

import com.seconize.taskmanager.entity.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String assignee;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
    private Long parentTaskId;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
        this.priority = task.getPriority().name();
        this.assignee = task.getAssignee();
        this.deadline = task.getDeadline();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.projectId = task.getProject().getId();

        this.parentTaskId = task.getParentTask() != null
                ? task.getParentTask().getId()
                : null;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getAssignee() {
        return assignee;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }
}
