
package com.seconize.taskmanager.dto;

import com.seconize.taskmanager.entity.TaskActivity;
import java.time.LocalDateTime;

public class TaskActivityResponse {

    private Long id;
    private Long taskId;
    private String action;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime createdAt;

    public TaskActivityResponse(TaskActivity activity) {
        this.id = activity.getId();
        this.taskId = activity.getTask().getId();
        this.action = activity.getAction();
        this.oldStatus = activity.getOldStatus();
        this.newStatus = activity.getNewStatus();
        this.createdAt = activity.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getAction() {
        return action;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
