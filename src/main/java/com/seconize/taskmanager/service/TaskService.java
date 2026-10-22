
package com.seconize.taskmanager.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.seconize.taskmanager.entity.Project;
import com.seconize.taskmanager.entity.Task;
import com.seconize.taskmanager.entity.TaskActivity;
import com.seconize.taskmanager.entity.TaskPriority;
import com.seconize.taskmanager.entity.TaskStatus;

import com.seconize.taskmanager.repository.ProjectRepository;
import com.seconize.taskmanager.repository.TaskRepository;
import com.seconize.taskmanager.repository.TaskActivityRepository;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskActivityRepository taskActivityRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            TaskActivityRepository taskActivityRepository) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.taskActivityRepository = taskActivityRepository;
    }

    // Record task activity in the database
    private void recordActivity(
            Task task,
            String action,
            String oldStatus,
            String newStatus) {

        TaskActivity activity = new TaskActivity();
        activity.setTask(task);
        activity.setAction(action);
        activity.setOldStatus(oldStatus);
        activity.setNewStatus(newStatus);

        taskActivityRepository.save(activity);
    }

    // Create a task under a project
    public Task createTask(Long projectId, Task task) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project not found with ID: " + projectId
                        ));

        task.setId(null);
        task.setProject(project);

        Task savedTask = taskRepository.save(task);

        recordActivity(
                savedTask,
                "TASK_CREATED",
                null,
                savedTask.getStatus() != null
                        ? savedTask.getStatus().name()
                        : null
        );

        return savedTask;
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Get a task by ID
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found with ID: " + id
                        ));
    }

    // Get tasks belonging to a project
    public List<Task> getTasksByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Project not found with ID: " + projectId
            );
        }

        return taskRepository.findByProjectId(projectId);
    }

    // Filter tasks by status
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    // Filter tasks by priority
    public List<Task> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    // Create a subtask under an existing task
    public Task createSubtask(Long parentTaskId, Task subtask) {

        Task parentTask = getTaskById(parentTaskId);

        subtask.setId(null);
        subtask.setProject(parentTask.getProject());
        subtask.setParentTask(parentTask);

        return taskRepository.save(subtask);
    }

    // Get subtasks belonging to a parent task
    public List<Task> getSubtasks(Long parentTaskId) {

        getTaskById(parentTaskId);

        return taskRepository.findByParentTaskId(parentTaskId);
    }

    // Update task fields and record status changes
    public Task updateTask(Long id, Task updatedTask) {

        Task existingTask = getTaskById(id);

        // Capture the original status before changing anything
        TaskStatus oldStatus = existingTask.getStatus();

        if (updatedTask.getTitle() != null) {
            existingTask.setTitle(updatedTask.getTitle());
        }

        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }

        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }

        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }

        if (updatedTask.getAssignee() != null) {
            existingTask.setAssignee(updatedTask.getAssignee());
        }

        if (updatedTask.getDeadline() != null) {
            existingTask.setDeadline(updatedTask.getDeadline());
        }

        Task savedTask = taskRepository.save(existingTask);

        TaskStatus newStatus = savedTask.getStatus();

        // Record history only if the status actually changed
        if (updatedTask.getStatus() != null
                && oldStatus != newStatus) {

            recordActivity(
                    savedTask,
                    "STATUS_CHANGED",
                    oldStatus != null ? oldStatus.name() : null,
                    newStatus != null ? newStatus.name() : null
            );
        }

        return savedTask;
    }

    // Delete a task, preventing deletion when it has subtasks
    public void deleteTask(Long id) {

        Task task = getTaskById(id);

        List<Task> subtasks = taskRepository.findByParentTaskId(id);

        if (!subtasks.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete task with ID: " + id
                            + " because it has subtasks. "
                            + "Delete its subtasks first."
            );
        }

        taskRepository.delete(task);
    }

    // Filter tasks by both status and priority
    public List<Task> getTasksByStatusAndPriority(
            TaskStatus status,
            TaskPriority priority) {

        return taskRepository.findByStatusAndPriority(status, priority);
    }
}
