
package com.seconize.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import jakarta.validation.constraints.Pattern;

public class TaskRequest {


    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Task title cannot exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Pattern(
            regexp = "^(TODO|IN_PROGRESS|BLOCKED|DONE)$",
            message = "Status must be TODO, IN_PROGRESS, BLOCKED, or DONE"
    )
    private String status;

    @Pattern(
            regexp = "^(LOW|MEDIUM|HIGH|URGENT)$",
            message = "Priority must be LOW, MEDIUM, HIGH, or URGENT"
    )
    private String priority;
    private String assignee;
    private LocalDate deadline;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

}
