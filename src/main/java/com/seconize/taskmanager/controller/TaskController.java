
package com.seconize.taskmanager.controller;

import com.seconize.taskmanager.dto.TaskRequest;
import com.seconize.taskmanager.dto.TaskResponse;
import com.seconize.taskmanager.entity.Task;
import com.seconize.taskmanager.entity.TaskPriority;
import com.seconize.taskmanager.entity.TaskStatus;
import com.seconize.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Filter tasks by status
    @GetMapping("/tasks/status/{status}")
    public List<TaskResponse> getTasksByStatus(
            @PathVariable TaskStatus status) {

        return taskService.getTasksByStatus(status)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    @GetMapping("/tasks/priority/{priority}")
    public List<TaskResponse> getTasksByPriority(
            @PathVariable TaskPriority priority) {
        return taskService.getTasksByPriority(priority)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    // Create a task under a project
    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request) {

        Task task = new Task();


        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssignee(request.getAssignee());
        task.setDeadline(request.getDeadline());

        if (request.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(request.getStatus()));
        }

        if (request.getPriority() != null) {
            task.setPriority(TaskPriority.valueOf(request.getPriority()));
        }

        return new TaskResponse(
                taskService.createTask(projectId, task)
        );
    }

    // Get all tasks
    @GetMapping("/tasks")
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    @GetMapping("/tasks/filter")
    public List<TaskResponse> getTasksByStatusAndPriority(
            @RequestParam TaskStatus status,
            @RequestParam TaskPriority priority) {

        return taskService
                .getTasksByStatusAndPriority(status, priority)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    // Get one task by ID
    @GetMapping("/tasks/{id:\\d+}")
    public TaskResponse getTaskById(@PathVariable Long id) {
        return new TaskResponse(taskService.getTaskById(id));
    }

    // Get all tasks belonging to a project
    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse> getTasksByProject(
            @PathVariable Long projectId) {
        return taskService.getTasksByProject(projectId)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {

        Task task = new Task();

        task.setStatus(null);
        task.setPriority(null);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssignee(request.getAssignee());
        task.setDeadline(request.getDeadline());

        if (request.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(request.getStatus()));
        }

        if (request.getPriority() != null) {
            task.setPriority(TaskPriority.valueOf(request.getPriority()));
        }

        return new TaskResponse(taskService.updateTask(id, task));
    }



    // Create a subtask under an existing task
    @PostMapping("/tasks/{parentTaskId}/subtasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createSubtask(
            @PathVariable Long parentTaskId,
            @Valid @RequestBody TaskRequest request) {

        Task subtask = new Task();

        subtask.setTitle(request.getTitle());
        subtask.setDescription(request.getDescription());
        subtask.setAssignee(request.getAssignee());
        subtask.setDeadline(request.getDeadline());

        if (request.getStatus() != null) {
            subtask.setStatus(TaskStatus.valueOf(request.getStatus()));
        }

        if (request.getPriority() != null) {
            subtask.setPriority(TaskPriority.valueOf(request.getPriority()));
        }

        return new TaskResponse(
                taskService.createSubtask(parentTaskId, subtask)
        );
    }


    // Get all subtasks belonging to a parent task
    @GetMapping("/tasks/{parentTaskId}/subtasks")
    public List<TaskResponse> getSubtasks(
            @PathVariable Long parentTaskId) {

        return taskService.getSubtasks(parentTaskId)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }



    // Delete a task
    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }



}
