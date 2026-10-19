
package com.seconize.taskmanager.service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.seconize.taskmanager.entity.Project;
import com.seconize.taskmanager.entity.Task;
import com.seconize.taskmanager.repository.ProjectRepository;
import com.seconize.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import com.seconize.taskmanager.entity.TaskStatus;
import com.seconize.taskmanager.entity.TaskPriority;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public Task createTask(Long projectId, Task task) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project not found with ID: " + projectId
                        ));

        task.setId(null);
        task.setProject(project);

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }


    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found with ID: " + id
                ));
    }


    public List<Task> getTasksByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Project not found with ID: " + projectId
            );
        }

        return taskRepository.findByProjectId(projectId);
    }
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }


    public Task createSubtask(Long parentTaskId, Task subtask) {

        Task parentTask = getTaskById(parentTaskId);

        subtask.setId(null);
        subtask.setProject(parentTask.getProject());
        subtask.setParentTask(parentTask);

        return taskRepository.save(subtask);
    }

    public List<Task> getSubtasks(Long parentTaskId) {

        getTaskById(parentTaskId);

        return taskRepository.findByParentTaskId(parentTaskId);
    }



    public Task updateTask(Long id, Task updatedTask) {

        Task existingTask = getTaskById(id);

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

        return taskRepository.save(existingTask);
    }

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

    public List<Task> getTasksByStatusAndPriority(
            TaskStatus status,
            TaskPriority priority) {

        return taskRepository.findByStatusAndPriority(status, priority);
    }
}
