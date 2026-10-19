
package com.seconize.taskmanager.controller;

import com.seconize.taskmanager.dto.AiTaskBreakdownRequest;
import com.seconize.taskmanager.dto.AiTaskSuggestion;
import com.seconize.taskmanager.service.AiTaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiTaskController {

    private final AiTaskService aiTaskService;

    public AiTaskController(AiTaskService aiTaskService) {
        this.aiTaskService = aiTaskService;
    }

    @PostMapping("/task-breakdown")
    public ResponseEntity<?> generateTaskBreakdown(
            @Valid @RequestBody AiTaskBreakdownRequest request) {

        try {
            List<AiTaskSuggestion> suggestions =
                    aiTaskService.generateBreakdown(
                            request.projectDescription());

            return ResponseEntity.ok(suggestions);

        } catch (IllegalStateException exception) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", exception.getMessage()));
        }
    }
}
