
package com.seconize.taskmanager.controller;

import com.seconize.taskmanager.entity.Project;
import com.seconize.taskmanager.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
    void createProjectShouldReturn201Created() throws Exception {
        Project project = new Project();
        project.setName("Test Project");

        when(projectService.createProject(any(Project.class)))
                .thenReturn(project);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Test Project"
                                }
                                """))
                .andExpect(status().isCreated());
    }
    @Test
    void getAllProjectsShouldReturn200OK() throws Exception {
        when(projectService.getAllProjects())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(
                        org.springframework.test.web.servlet.request
                                .MockMvcRequestBuilders.get("/api/projects")
                )
                .andExpect(status().isOk());
    }
    @Test
    void getProjectByIdShouldReturn200OK() throws Exception {
        Project project = new Project();
        project.setName("Test Project");

        when(projectService.getProjectById(1L))
                .thenReturn(project);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/projects/1")
                )
                .andExpect(status().isOk());
    }
    @Test
    void updateProjectShouldReturn200OK() throws Exception {
        Project project = new Project();
        project.setName("Updated Project");

        when(projectService.updateProject(
                org.mockito.ArgumentMatchers.eq(1L),
                any(Project.class)
        )).thenReturn(project);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/projects/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "name": "Updated Project"
                                    }
                                    """)
                )
                .andExpect(status().isOk());
    }
    @Test
    void deleteProjectShouldReturn204NoContent() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/projects/1")
                )
                .andExpect(status().isNoContent());
    }
}
