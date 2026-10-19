
package com.seconize.taskmanager.service;

import com.seconize.taskmanager.entity.Project;
import com.seconize.taskmanager.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void getProjectByIdShouldThrowNotFoundWhenProjectDoesNotExist() {
        // Arrange: simulate a project ID that does not exist
        when(projectRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act and Assert: verify that the service throws 404 Not Found
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.getProjectById(999L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getProjectByIdShouldReturnProjectWhenProjectExists() {
        // Arrange
        Project project = new Project();
        project.setName("Test Project");

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        // Act
        Project result = projectService.getProjectById(1L);

        // Assert
        assertEquals("Test Project", result.getName());
    }

    @Test
    void createProjectShouldSaveProject() {
        // Arrange
        Project project = new Project();
        project.setName("New Test Project");

        when(projectRepository.save(project)).thenReturn(project);

        // Act
        Project result = projectService.createProject(project);

        // Assert
        assertEquals("New Test Project", result.getName());
        org.mockito.Mockito.verify(projectRepository).save(project);
    }


    @Test
    void updateProjectShouldUpdateExistingProject() {
        // Arrange
        Project existingProject = new Project();
        existingProject.setName("Old Project Name");

        Project updatedProject = new Project();
        updatedProject.setName("Updated Project Name");
        updatedProject.setDescription("Updated description");

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(existingProject));
        when(projectRepository.save(existingProject))
                .thenReturn(existingProject);

        // Act
        Project result = projectService.updateProject(1L, updatedProject);

        // Assert
        assertEquals("Updated Project Name", result.getName());
        assertEquals("Updated description", result.getDescription());
    }


    @Test
    void deleteProjectShouldDeleteExistingProject() {
        // Arrange
        Project existingProject = new Project();
        existingProject.setName("Project to Delete");

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(existingProject));

        // Act
        projectService.deleteProject(1L);

        // Assert
        org.mockito.Mockito.verify(projectRepository)
                .delete(existingProject);
    }


}
