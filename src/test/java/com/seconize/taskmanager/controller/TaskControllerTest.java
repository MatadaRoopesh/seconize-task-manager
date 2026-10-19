
package com.seconize.taskmanager.controller;

import com.seconize.taskmanager.entity.Project;
import com.seconize.taskmanager.entity.Task;
import com.seconize.taskmanager.service.TaskService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.seconize.taskmanager.entity.TaskStatus;


@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void createTaskShouldReturn201Created() throws Exception {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        Task task = new Task();
        task.setId(10L);
        task.setTitle("Test Task");
        task.setProject(project);

        when(taskService.createTask(eq(1L), any(Task.class)))
                .thenReturn(task);

        mockMvc.perform(post("/api/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Test Task",
                                    "description": "Testing task creation",
                                    "status": "TODO",
                                    "priority": "HIGH",
                                    "assignee": "Roopesh"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllTasksShouldReturn200OK() throws Exception {
        when(taskService.getAllTasks())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void getTaskByIdShouldReturn200OK() throws Exception {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        Task task = new Task();
        task.setId(10L);
        task.setTitle("Test Task");
        task.setProject(project);

        when(taskService.getTaskById(10L))
                .thenReturn(task);

        mockMvc.perform(get("/api/tasks/10"))
                .andExpect(status().isOk());
    }

    @Test
    void getTasksByStatusShouldReturn200OK() throws Exception {
        when(taskService.getTasksByStatus(TaskStatus.TODO))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tasks/status/TODO"))
                .andExpect(status().isOk());
    }
}
