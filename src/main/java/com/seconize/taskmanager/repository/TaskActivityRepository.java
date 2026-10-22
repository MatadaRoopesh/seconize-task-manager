
package com.seconize.taskmanager.repository;

import com.seconize.taskmanager.entity.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskActivityRepository
        extends JpaRepository<TaskActivity, Long> {

    List<TaskActivity> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}
