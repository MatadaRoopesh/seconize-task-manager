
package com.seconize.taskmanager.repository;

import com.seconize.taskmanager.entity.Task;
import com.seconize.taskmanager.entity.TaskStatus;
import com.seconize.taskmanager.entity.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    // Filter tasks by status
    List<Task> findByStatus(TaskStatus status);

    // Filter tasks by priority
    List<Task> findByPriority(TaskPriority priority);

    List<Task> findByStatusAndPriority(
            TaskStatus status,
            TaskPriority priority
    );
    // Find subtasks belonging to a parent task
    List<Task> findByParentTaskId(Long parentTaskId);
}
