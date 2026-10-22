
package com.seconize.taskmanager.controller;

import com.seconize.taskmanager.dto.TaskActivityResponse;
import com.seconize.taskmanager.repository.TaskActivityRepository;
import com.seconize.taskmanager.service.TaskService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskActivityController {

    private final TaskActivityRepository taskActivityRepository;
    private final TaskService taskService;

    public TaskActivityController(
            TaskActivityRepository taskActivityRepository,
            TaskService taskService) {
        this.taskActivityRepository = taskActivityRepository;
        this.taskService = taskService;
    }

    @GetMapping("/{taskId}/activity")
    public List<TaskActivityResponse> getTaskActivity(
            @PathVariable Long taskId) {

        // Verify the task exists
        taskService.getTaskById(taskId);

        return taskActivityRepository
                .findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream()
                .map(TaskActivityResponse::new)
                .toList();
    }
}
