
package com.seconize.taskmanager.service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.seconize.taskmanager.entity.Project;
import com.seconize.taskmanager.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }


    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project not found with ID: " + id
                        ));
    }


    public Project updateProject(Long id, Project updatedProject) {
        Project existingProject = getProjectById(id);

        existingProject.setName(updatedProject.getName());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setStatus(updatedProject.getStatus());
        existingProject.setStartDate(updatedProject.getStartDate());
        existingProject.setDeadline(updatedProject.getDeadline());

        return projectRepository.save(existingProject);
    }

    public void deleteProject(Long id) {
        Project existingProject = getProjectById(id);
        projectRepository.delete(existingProject);
    }
}

