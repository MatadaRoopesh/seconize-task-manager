
package com.seconize.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiTaskBreakdownRequest(

        @NotBlank(message = "Project description is required")
        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String projectDescription

) {
}
