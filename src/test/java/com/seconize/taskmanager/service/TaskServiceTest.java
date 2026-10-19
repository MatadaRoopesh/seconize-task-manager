package com.seconize.taskmanager.service;

import com.seconize.taskmanager.entity.Task;
import com.seconize.taskmanager.repository.ProjectRepository;
import com.seconize.taskmanager.repository.TaskRepository;
import com.seconize.taskmanager.entity.Project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void deleteTaskShouldFailWhenTaskHasSubtasks() {
        // Arrange: simulate an existing parent task.
        Task parentTask = new Task();

        when(taskRepository.findById(3L))
                .thenReturn(Optional.of(parentTask));

        // Simulate the parent having one subtask.
        when(taskRepository.findByParentTaskId(3L))
                .thenReturn(List.of(new Task()));

        // Act and Assert: deletion should return HTTP 409 Conflict.
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.deleteTask(3L)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        // Verify that the parent task was not deleted.
        verify(taskRepository, never()).delete(parentTask);
    }

    @Test
    void deleteTaskShouldSucceedWhenTaskHasNoSubtasks() {
        Task task = new Task();

        when(taskRepository.findById(7L))
                .thenReturn(Optional.of(task));

        when(taskRepository.findByParentTaskId(7L))
                .thenReturn(List.of());

        taskService.deleteTask(7L);

        verify(taskRepository).delete(task);
    }
    @Test
    void getTaskByIdShouldThrowNotFoundWhenTaskDoesNotExist() {
        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.getTaskById(999L)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(taskRepository).findById(999L);
    }

    @Test
    void createTaskShouldThrowNotFoundWhenProjectDoesNotExist() {
        when(projectRepository.findById(999L))
                .thenReturn(Optional.empty());

        Task task = new Task();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> taskService.createTask(999L, task)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(taskRepository, never()).save(task);
    }

    @Test
    void createSubtaskShouldInheritParentProject() {
        Project project = new Project();
        Task parentTask = new Task();
        parentTask.setProject(project);

        when(taskRepository.findById(3L))
                .thenReturn(Optional.of(parentTask));

        Task subtask = new Task();

        when(taskRepository.save(subtask))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task savedSubtask = taskService.createSubtask(3L, subtask);

        assertEquals(project, savedSubtask.getProject());
        assertEquals(parentTask, savedSubtask.getParentTask());

        verify(taskRepository).save(subtask);
    }

    @Test
    void getSubtasksShouldReturnSubtasksForParentTask() {
        Task parentTask = new Task();
        Task subtask = new Task();

        when(taskRepository.findById(3L))
                .thenReturn(Optional.of(parentTask));

        when(taskRepository.findByParentTaskId(3L))
                .thenReturn(List.of(subtask));

        List<Task> result = taskService.getSubtasks(3L);

        assertEquals(1, result.size());
        assertEquals(subtask, result.get(0));

        verify(taskRepository).findByParentTaskId(3L);
    }
}